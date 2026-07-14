{{- define "java-app.fullname" -}}
{{- printf "%s-java-app" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "java-app.selectorLabels" -}}
app.kubernetes.io/name: java-app
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}
