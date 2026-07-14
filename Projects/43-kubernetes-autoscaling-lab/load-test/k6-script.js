// k6 load profile for the autoscaling lab.
//
// Target: the CPU-burning /work endpoint, reached through
//   kubectl port-forward svc/autoscaling-java-app 8080:80
//
// Run (local k6):            k6 run load-test/k6-script.js
// Run (k6 via Docker):       docker run --rm -i \
//                              -e BASE_URL=http://host.docker.internal:8080 \
//                              grafana/k6 run - < load-test/k6-script.js
//                              On Linux, use the in-cluster BusyBox generator
//                              if host.docker.internal cannot reach the host.
//
// The ramp shape is deliberate: ramp up so you can watch CPU climb, hold long
// enough for the HPA (15s metric loop) to add pods, then ramp down and watch
// the 300s scale-down window do its job.
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '1m', target: 20 }, // ramp up:   CPU begins to rise
    { duration: '3m', target: 60 }, // steady:    hold load above the 60% target
    { duration: '1m', target: 0 },  // ramp down: let CPU fall, watch scale-down
  ],
};

export default function () {
  // Each request burns ~200ms of CPU on some pod (capped by MAX_WORK_MS).
  const res = http.get(`${BASE_URL}/work?ms=200`);
  check(res, { 'status is 200': (r) => r.status === 200 });
  sleep(0.5);
}
