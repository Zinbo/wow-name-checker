import fs from 'fs';
import path from 'path';


const realmsDirectory = path.join(process.cwd(), 'realms');

export interface Server {
    realm: string,
    region: string
}

export default function getServers(): Server[] {
    const regions = fs.readdirSync(realmsDirectory);
    return regions.flatMap(region => {
        const regionFile = path.join(realmsDirectory, region);
        const realmsInRegion = fs.readFileSync(regionFile, 'utf8');
        region = region.replace(/\.txt$/, '');
        region = region.charAt(0).toUpperCase() + region.slice(1);
        return realmsInRegion.split("\n").map(realm => ({region, realm}));
    });
}