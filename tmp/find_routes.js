const fs = require("fs");
const code = fs.readFileSync("/tmp/main.js", "utf8");
const r = /"(\/api\/[^"]+)"|'(\/api\/[^']+)'/g;
let match;
const endpoints = new Set();
while ((match = r.exec(code)) !== null) {
  endpoints.add(match[1] || match[2]);
}
console.log(Array.from(endpoints).slice(0, 50));

const hostMatches = code.match(/preview[^"'\s`<>]+/g) || [];
console.log("Preview keywords:", Array.from(new Set(hostMatches)).slice(0, 20));
