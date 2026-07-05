{{- define "rollout.fullname" -}}
{{- printf "%s-%s" .Release.Name "java-app" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Render one Deployment for a track.
Usage: include "rollout.deployment" (dict "ctx" . "track" "blue" "tag" .Values.image.blueTag "replicas" .Values.blue.replicas)
*/}}
{{- define "rollout.deployment" -}}
{{- $ctx := .ctx -}}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "rollout.fullname" $ctx }}-{{ .track }}
  labels:
    app: java-app
    track: {{ .track }}
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
    spec:
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
          livenessProbe:
            httpGet:
              path: /health
              port: http
          resources:
            {{- toYaml $ctx.Values.resources | nindent 12 }}
{{- end -}}
