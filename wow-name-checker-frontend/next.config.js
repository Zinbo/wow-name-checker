/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  images: {
    unoptimized: true
  },
  async rewrites() {
    return [
      {
        source: '/profile/:path*',
        destination: 'http://backend-svc:8080/profile/:path*',
      },
    ]
  },
  output: 'standalone',
}

module.exports = nextConfig
