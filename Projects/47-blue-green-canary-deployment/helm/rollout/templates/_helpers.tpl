{{- define "rollout.fullname" -}}
{{- printf "%s-%s" .Release.Name "java-app" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Render one secure Deployment for a blue-green track. */}}
{{- define "rollout.deployment" -}}
{{- $ctx := .ctx -}}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "rollout.fullname" $ctx }}-{{ .track }}
  labels:
    app: java-app
    track: {{ .track }}
    version: {{ .tag | quote }}
spec:
  replicas: {{ .replicas }}
  selector:
    matchLabels:
      app: java-app
      track: {{ .track }}
  template:
    metadata:
      labels:
        app: java-app
        track: {{ .track }}
        version: {{ .tag | quote }}
    spec:
      securityContext:
        runAsNonRoot: true
        runAsUser: 10001
        seccompProfile:
          type: RuntimeDefault
      containers:
        - name: app
          image: "{{ $ctx.Values.image.repository }}:{{ .tag }}"
          imagePullPolicy: {{ $ctx.Values.image.pullPolicy }}
          ports:
            - name: http
              containerPort: {{ $ctx.Values.containerPort }}
          env:
            - name: APP_VERSION
              value: {{ .tag | quote }}
          readinessProbe:
            httpGet:
              path: /ready
              port: http
            initialDelaySeconds: 3
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /health
              port: http
            initialDelaySeconds: 5
            periodSeconds: 10
          resources:
            {{- toYaml $ctx.Values.resources | nindent 12 }}
          securityContext:
            allowPrivilegeEscalation: false
            readOnlyRootFilesystem: true
            capabilities:
              drop: ["ALL"]
          volumeMounts:
            - name: tmp
              mountPath: /tmp
      volumes:
        - name: tmp
          emptyDir: {}
{{- end -}}
