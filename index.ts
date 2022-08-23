import { serve } from "https://deno.land/std@0.152.0/http/server.ts";
import { router } from "https://crux.land/router@0.0.5";

import { routes } from "./dist/api/api.js";

console.log(routes);

await serve(router(routes), { port: 1338 });
