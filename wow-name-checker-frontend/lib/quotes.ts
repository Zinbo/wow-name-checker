import fs from 'fs';
import path from 'path';


export default function getQuotes() : string[] {
    return JSON.parse(fs.readFileSync(path.join(process.cwd(), 'quotes.json'), 'utf8'));
}