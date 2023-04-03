variable "mysql_password" {
  type = string
}

variable "wow_client_id" {
  type = string
}

variable "wow_client_secret" {
  type = string
}

variable "mailtrap_username" {
  type = string
}

variable "mailtrap_password" {
  type = string
}

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.16"
    }
  }

  backend "s3" {
    region = "eu-west-2"
    key    = "terraform.tfstate"
    bucket = "wow-name-checker-terraform-state"
  }

  required_version = ">= 1.2.0"
}

provider "aws" {
  region = "eu-west-2"
}

resource "aws_vpc" "vpc_terraform" {
  cidr_block           = "10.1.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    Name = "Terraform VPC"
  }
}

resource "aws_internet_gateway" "internet_gateway" {
  vpc_id = aws_vpc.vpc_terraform.id
}

resource "aws_subnet" "pub_subnet_terraform_2a" {
  vpc_id            = aws_vpc.vpc_terraform.id
  cidr_block        = "10.1.16.0/20"
  availability_zone = "eu-west-2a"
}

resource "aws_subnet" "pub_subnet_terraform_2b" {
  vpc_id            = aws_vpc.vpc_terraform.id
  cidr_block        = "10.1.32.0/20"
  availability_zone = "eu-west-2b"
}

resource "aws_subnet" "pub_subnet_terraform_2c" {
  vpc_id            = aws_vpc.vpc_terraform.id
  cidr_block        = "10.1.0.0/20"
  availability_zone = "eu-west-2c"
}

resource "aws_route_table" "public_terraform" {
  vpc_id = aws_vpc.vpc_terraform.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.internet_gateway.id
  }
}

resource "aws_route_table_association" "route_table_association_1" {
  subnet_id      = aws_subnet.pub_subnet_terraform_2a.id
  route_table_id = aws_route_table.public_terraform.id
}

resource "aws_route_table_association" "route_table_association_2" {
  subnet_id      = aws_subnet.pub_subnet_terraform_2b.id
  route_table_id = aws_route_table.public_terraform.id
}

resource "aws_route_table_association" "route_table_association_3" {
  subnet_id      = aws_subnet.pub_subnet_terraform_2c.id
  route_table_id = aws_route_table.public_terraform.id

}

resource "aws_security_group" "ecs_sg_terraform" {
  vpc_id = aws_vpc.vpc_terraform.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["86.19.97.38/32"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "rds_sg_terraform" {
  vpc_id = aws_vpc.vpc_terraform.id

  ingress {
    protocol        = "tcp"
    from_port       = 3306
    to_port         = 3306
    security_groups = [aws_security_group.ecs_sg_terraform.id]
  }

}

resource "aws_security_group" "ec_sg_terraform" {
  vpc_id = aws_vpc.vpc_terraform.id

  ingress {
    protocol        = "tcp"
    from_port       = 6379
    to_port         = 6379
    security_groups = [aws_security_group.ecs_sg_terraform.id]
  }

}




## Autoscaling Group
data "aws_iam_policy_document" "ecs_agent_terraform" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_agent_terraform" {
  name               = "ecs-agent"
  assume_role_policy = data.aws_iam_policy_document.ecs_agent_terraform.json
}

resource "aws_iam_role" "ecs_task_execution_terraform" {
  name               = "ecs-task_execution_terraform"
  assume_role_policy = data.aws_iam_policy_document.ecs_agent_terraform.json
}


resource "aws_iam_role_policy_attachment" "ecs_agent_terraform" {
  role       = aws_iam_role.ecs_agent_terraform.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs_agent_terraform" {
  name = "ecs_agent_terraform"
  role = aws_iam_role.ecs_agent_terraform.name
}

# Role for ECS execution
data "aws_iam_policy_document" "ecs_task_execution_role_terraform" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_task_execution_role_terraform" {
  name               = "ecs_agent_terraform"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_execution_role_terraform.json
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_terraform" {
  role       = aws_iam_role.ecs_task_execution_role_terraform.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_launch_template" "ecs_launch_config_terraform" {
  image_id = "ami-07e394e4df20de8d2"
  iam_instance_profile {
    name = aws_iam_instance_profile.ecs_agent_terraform.name
  }
  user_data     = base64encode("#!/bin/bash\necho ECS_CLUSTER=wow-name-checker-cluster_terraform >> /etc/ecs/ecs.config")
  instance_type = "t2.micro"
  network_interfaces {
    associate_public_ip_address = true
    security_groups             = [aws_security_group.ecs_sg_terraform.id]
  }
  name     = "ecs_launch_config_terraform"
  key_name = "microk8s-node-key-pair"
}

resource "aws_autoscaling_group" "failure_analysis_ecs_asg" {
  name                = "asg"
  vpc_zone_identifier = [aws_subnet.pub_subnet_terraform_2a.id, aws_subnet.pub_subnet_terraform_2b.id, aws_subnet.pub_subnet_terraform_2c.id]
  launch_template {
    id      = aws_launch_template.ecs_launch_config_terraform.id
    version = "$Latest"
  }

  desired_capacity          = 1
  min_size                  = 1
  max_size                  = 1
  health_check_grace_period = 300
  health_check_type         = "EC2"
}

## RDS
resource "aws_db_subnet_group" "db_subnet_group_terraform" {
  subnet_ids = [aws_subnet.pub_subnet_terraform_2a.id, aws_subnet.pub_subnet_terraform_2b.id, aws_subnet.pub_subnet_terraform_2c.id]
}


resource "aws_db_instance" "wow-name-checker-db_terraform" {
  identifier             = "wow-name-checker-terraform"
  allocated_storage      = 5
  maintenance_window     = "sun:03:00-sun:03:30"
  multi_az               = false
  engine                 = "mysql"
  engine_version         = "8.0.28"
  instance_class         = "db.t2.micro"
  db_name                = "wow_name_checker"
  username               = "admin"
  password               = var.mysql_password
  port                   = "3306"
  db_subnet_group_name   = aws_db_subnet_group.db_subnet_group_terraform.id
  vpc_security_group_ids = [aws_security_group.rds_sg_terraform.id, aws_security_group.ecs_sg_terraform.id]
  publicly_accessible    = false
  skip_final_snapshot    = true
}


# Elasticache
resource "aws_elasticache_subnet_group" "ec_subnet_group_terraform" {
  name       = "my-cache-subnet"
  subnet_ids = [aws_subnet.pub_subnet_terraform_2a.id, aws_subnet.pub_subnet_terraform_2b.id, aws_subnet.pub_subnet_terraform_2c.id]
}

resource "aws_elasticache_cluster" "wow-name-checker-cache-terraform" {
  cluster_id           = "wow-name-checker-cache-terraform"
  engine               = "redis"
  node_type            = "cache.t2.micro"
  num_cache_nodes      = 1
  parameter_group_name = "default.redis7"
  engine_version       = "7.0"
  port                 = 6379
  subnet_group_name    = aws_elasticache_subnet_group.ec_subnet_group_terraform.name
  security_group_ids   = [aws_security_group.ec_sg_terraform.id]

}


# ECS
resource "aws_ecr_repository" "wow-name-checker-backend_terraform" {
  name = "wow-name-checker-backend_terraform"
  force_delete = true
}

resource "aws_ecr_repository" "wow-name-checker-frontend_terraform" {
  name = "wow-name-checker-frontend_terraform"
  force_delete = true
}

resource "aws_ecs_cluster" "wow-name-checker-cluster_terraform" {
  name = "wow-name-checker-cluster_terraform"
}

resource "aws_ecs_cluster_capacity_providers" "wow-name-checker-cluster-providers" {
  cluster_name = aws_ecs_cluster.wow-name-checker-cluster_terraform.name

  capacity_providers = [aws_ecs_capacity_provider.wow-name-checker-cluster-provider.name]

  default_capacity_provider_strategy {
    base              = 1
    weight            = 100
    capacity_provider = aws_ecs_capacity_provider.wow-name-checker-cluster-provider.name
  }
}

resource "aws_ecs_capacity_provider" "wow-name-checker-cluster-provider" {
  name = "example"

  auto_scaling_group_provider {
    auto_scaling_group_arn = aws_autoscaling_group.failure_analysis_ecs_asg.arn
  }
}

resource "aws_ecs_task_definition" "backend-task_definition_terraform" {
  family = "wow-name-checker-backend_terraform"
  container_definitions = jsonencode([
    {
      name : "backend-container",
      image : aws_ecr_repository.wow-name-checker-backend_terraform.repository_url
      cpu : 0,
      portMappings : [
        {
          name : "profile-api-port",
          containerPort : 8080,
          hostPort : 8080,
          protocol : "tcp",
          appProtocol : "http"
        }
      ],
      essential : true,
      environment : [
        {
          name : "MYSQL_USERNAME",
          value : aws_db_instance.wow-name-checker-db_terraform.username
        },
        {
          name : "MYSQL_PASSWORD",
          value : aws_db_instance.wow-name-checker-db_terraform.password
        },
        {
          name : "WOW_CLIENT_ID",
          value : var.wow_client_id
        },
        {
          name : "MYSQL_URL",
          value : "jdbc:mysql://${aws_db_instance.wow-name-checker-db_terraform.endpoint}/${aws_db_instance.wow-name-checker-db_terraform.db_name}"
        },
        {
          name : "WOW_CLIENT_SECRET",
          value : var.wow_client_secret
        },
        {
          name : "REDIS_URL",
          value : aws_elasticache_cluster.wow-name-checker-cache-terraform.cache_nodes[0].address
        }
      ],
      mountPoints : [],
      volumesFrom : []
    }
  ])
  task_role_arn            = aws_iam_role.ecs_task_execution_role_terraform.arn
  execution_role_arn       = aws_iam_role.ecs_task_execution_role_terraform.arn
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]
  memory                   = "700"
}

resource "aws_ecs_task_definition" "frontend-task_definition_terraform" {
  family = "wow-name-checker-frontend_terraform"
  container_definitions = jsonencode([
    {
      name : "frontend-container",
      image : aws_ecr_repository.wow-name-checker-frontend_terraform.repository_url
      cpu : 0,
      portMappings : [
        {
          name : "wow-name-checker-frontend-3000-tcp",
          containerPort : 3000,
          hostPort : 80,
          protocol : "tcp",
          appProtocol : "http"
        }
      ],
      essential : true,
      environment : [],
      mountPoints : [],
      volumesFrom : []
    }
  ])
  execution_role_arn       = aws_iam_role.ecs_task_execution_role_terraform.arn
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]
  cpu                      = "256"
  memory                   = "205"
}

resource "aws_service_discovery_http_namespace" "wow-name-checker-cluster_terraform" {
  name        = "wow-name-checker-cluster_terraform"
  description = "Namespace for service discovery"
}

resource "aws_ecs_service" "wow-name-checker-backend_terraform" {
  name                               = "wow-name-checker-backend_terraform"
  cluster                            = aws_ecs_cluster.wow-name-checker-cluster_terraform.id
  task_definition                    = aws_ecs_task_definition.backend-task_definition_terraform.arn
  desired_count                      = 1
  deployment_maximum_percent         = 100
  deployment_minimum_healthy_percent = 0
  service_connect_configuration {
    enabled   = true
    namespace = aws_service_discovery_http_namespace.wow-name-checker-cluster_terraform.arn
    service {
      discovery_name = "backend-app"
      port_name      = "profile-api-port"
      client_alias {
        dns_name = "backend-svc"
        port     = "8080"
      }
    }
  }
}

resource "aws_ecs_service" "wow-name-checker-frontend_terraform" {
  name                               = "wow-name-checker-frontend_terraform"
  cluster                            = aws_ecs_cluster.wow-name-checker-cluster_terraform.id
  task_definition                    = aws_ecs_task_definition.frontend-task_definition_terraform.arn
  desired_count                      = 1
  deployment_maximum_percent         = 100
  deployment_minimum_healthy_percent = 0
  service_connect_configuration {
    enabled   = true
    namespace = aws_service_discovery_http_namespace.wow-name-checker-cluster_terraform.arn
  }
}