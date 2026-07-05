{{- define "java-app.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "java-app.fullname" -}}
{{- printf "%s-%s" .Release.Name (include "java-app.name" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "java-app.labels" -}}
app.kubernetes.io/name: {{ include "java-app.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}
