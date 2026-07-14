# Repository Rename Execution Plan

## Purpose

This file records the planned collision-safe folder rename and reorder operation for the 50 direct child folders under `Projects/`. The operation changes repository organization only; nested project contents remain intact.

## Source Audit

The mapping and difficulty levels come from `REPO_AUDIT_AND_ORDER_PLAN.md`, especially its **Recommended Final Order** section. Preflight inspection on 2026-07-14 confirmed that every source exists, every destination is absent, every source contains files, and every project has a README.

## Rename Mapping

| Old path | New path | Old # | New # | Project | Level | Status |
|---|---|---:|---:|---|---|---|
| `Projects/00 (Calculator)` | `Projects/01-simple-calculator` | 00 | 01 | Simple Calculator | Beginner | PLANNED |
| `Projects/01 (University)` | `Projects/02-university-management` | 01 | 02 | University Management | Beginner+ | PLANNED |
| `Projects/02 (Hydraulic)` | `Projects/03-hydraulic-network-simulator` | 02 | 03 | Hydraulic Network Simulator | Intermediate | PLANNED |
| `Projects/03 (Diet)` | `Projects/04-diet-takeaway-management` | 03 | 04 | Diet and Takeaway Management | Intermediate | PLANNED |
| `Projects/07 (StudentGradeManager)` | `Projects/05-student-grade-manager` | 07 | 05 | Student Grade Manager | Beginner+ | PLANNED |
| `Projects/12 (ProductInventoryManager)` | `Projects/06-product-inventory-manager` | 12 | 06 | Product Inventory Manager | Beginner+ | PLANNED |
| `Projects/09 (BankAccountSimulator)` | `Projects/07-bank-account-simulator` | 09 | 07 | Bank Account Simulator | Intermediate | PLANNED |
| `Projects/08 (LibraryManagementSystem)` | `Projects/08-library-management-system` | 08 | 08 | Library Management System | Intermediate | PLANNED |
| `Projects/14 (HotelRoomBookingSystem)` | `Projects/09-hotel-room-booking` | 14 | 09 | Hotel Room Booking | Beginner+ | PLANNED |
| `Projects/15 (EventRegistrationSystem)` | `Projects/10-event-registration-system` | 15 | 10 | Event Registration | Beginner+ | PLANNED |
| `Projects/16 (HospitalQueueManagement)` | `Projects/11-hospital-queue-management` | 16 | 11 | Hospital Queue Management | Beginner+ | PLANNED |
| `Projects/17 (RestaurantOrderingSystem)` | `Projects/12-restaurant-ordering-system` | 17 | 12 | Restaurant Ordering | Intermediate | PLANNED |
| `Projects/18 (QuizExamPlatform)` | `Projects/13-quiz-exam-platform` | 18 | 13 | Quiz / Exam Platform | Intermediate | PLANNED |
| `Projects/10 (ParkingGarageSystem)` | `Projects/14-parking-garage-system` | 10 | 14 | Parking Garage | Intermediate | PLANNED |
| `Projects/11 (MovieTicketBookingSystem)` | `Projects/15-movie-ticket-booking-system` | 11 | 15 | Movie Ticket Booking | Intermediate | PLANNED |
| `Projects/13 (TrainTicketReservationSystem)` | `Projects/16-train-ticket-reservation-system` | 13 | 16 | Train Ticket Reservation | Intermediate | PLANNED |
| `Projects/25 (MiniECommerceBackend)` | `Projects/17-mini-ecommerce-backend` | 25 | 17 | Mini E-Commerce Backend | Intermediate+ | PLANNED |
| `Projects/27 (NotificationService)` | `Projects/18-notification-service` | 27 | 18 | Notification Service | Intermediate+ | PLANNED |
| `Projects/19 (FileBasedAddressBook)` | `Projects/19-file-based-address-book` | 19 | 19 | File-Based Address Book | Intermediate | PLANNED |
| `Projects/20 (ExpenseTracker)` | `Projects/20-expense-tracker` | 20 | 20 | Expense Tracker | Intermediate | PLANNED |
| `Projects/29 (JobApplicationTracker)` | `Projects/21-job-application-tracker` | 29 | 21 | Job Application Tracker | Intermediate | PLANNED |
| `Projects/28 (CSVAnalyticsEngine)` | `Projects/22-csv-analytics-engine` | 28 | 22 | CSV Analytics Engine | Intermediate+ | PLANNED |
| `Projects/04 (MountainHuts)` | `Projects/23-mountain-huts-data-analysis` | 04 | 23 | Mountain Huts Data Analysis | Intermediate+ | PLANNED |
| `Projects/21 (TaskManagerJDBC)` | `Projects/24-task-manager-jdbc` | 21 | 24 | Task Manager JDBC | Intermediate+ | PLANNED |
| `Projects/22 (URLShortenerBackend)` | `Projects/25-url-shortener-backend` | 22 | 25 | URL Shortener Backend | Backend | PLANNED |
| `Projects/23 (ContactsRESTAPI)` | `Projects/26-contacts-rest-api` | 23 | 26 | Contacts REST API | Backend | PLANNED |
| `Projects/24 (BlogAPI)` | `Projects/27-blog-api` | 24 | 27 | Blog API | Backend | PLANNED |
| `Projects/26 (AuthenticationSystem)` | `Projects/28-authentication-system` | 26 | 28 | Authentication System | Advanced Java | PLANNED |
| `Projects/05 (Social)` | `Projects/29-social-network-jpa` | 05 | 29 | Social Network JPA | Advanced Java | PLANNED |
| `Projects/06 (WeatherSystem)` | `Projects/30-weather-system-jpa` | 06 | 30 | Weather System JPA | Advanced Java | PLANNED |
| `Projects/32 (TestingCoverageQualityGate)` | `Projects/31-testing-coverage-quality-gate` | 32 | 31 | Testing Coverage Quality Gate | DevOps | PLANNED |
| `Projects/31 (CIPipelineJavaApp)` | `Projects/32-ci-pipeline-java-app` | 31 | 32 | CI Pipeline Java App | DevOps | PLANNED |
| `Projects/30 (DockerizedJavaPostgres)` | `Projects/33-dockerized-java-postgresql` | 30 | 33 | Dockerized Java PostgreSQL | Backend | PLANNED |
| `Projects/33 (DockerComposeFullStack)` | `Projects/34-docker-compose-full-stack` | 33 | 34 | Docker Compose Full Stack | Resume-level | PLANNED |
| `Projects/34 (TerraformInfrastructureStarter)` | `Projects/35-terraform-infrastructure-starter` | 34 | 35 | Terraform Infrastructure Starter | DevOps | PLANNED |
| `Projects/35 (AnsibleServerConfiguration)` | `Projects/36-ansible-server-configuration` | 35 | 36 | Ansible Server Configuration | DevOps | PLANNED |
| `Projects/36 (KubernetesDeploymentJavaApp)` | `Projects/37-kubernetes-java-deployment` | 36 | 37 | Kubernetes Deployment Java App | DevOps | PLANNED |
| `Projects/37 (HelmChartJavaApp)` | `Projects/38-helm-chart-java-app` | 37 | 38 | Helm Chart Java App | DevOps | PLANNED |
| `Projects/38 (PrometheusGrafanaMonitoring)` | `Projects/39-prometheus-grafana-monitoring` | 38 | 39 | Prometheus Grafana Monitoring | Advanced DevOps | PLANNED |
| `Projects/39 (CentralizedLoggingStack)` | `Projects/40-centralized-logging-stack` | 39 | 40 | Centralized Logging Stack | Advanced DevOps | PLANNED |
| `Projects/41 (MicroservicesOrderSystem)` | `Projects/41-microservices-order-system` | 41 | 41 | Microservices Order System | Resume-level | PLANNED |
| `Projects/40 (GitOpsDeploymentPlatform)` | `Projects/42-gitops-deployment-platform` | 40 | 42 | GitOps Deployment Platform | Advanced DevOps | PLANNED |
| `Projects/42 (KubernetesAutoscalingLab)` | `Projects/43-kubernetes-autoscaling-lab` | 42 | 43 | Kubernetes Autoscaling Lab | Advanced DevOps | PLANNED |
| `Projects/43 (SecureCICDPipeline)` | `Projects/44-secure-ci-cd-pipeline` | 43 | 44 | Secure CI/CD Pipeline | Resume-level | PLANNED |
| `Projects/45 (InfrastructureAsCodeEnvironment)` | `Projects/45-infrastructure-as-code-environment` | 45 | 45 | Infrastructure as Code Environment | Advanced DevOps | PLANNED |
| `Projects/44 (FullObservabilityPlatform)` | `Projects/46-full-observability-platform` | 44 | 46 | Full Observability Platform | Resume-level | PLANNED |
| `Projects/47 (BlueGreenCanaryDeployment)` | `Projects/47-blue-green-canary-deployment` | 47 | 47 | Blue-Green and Canary Deployment | Advanced DevOps | PLANNED |
| `Projects/48 (MultiEnvironmentCloudNativeApp)` | `Projects/48-multi-environment-cloud-native-app` | 48 | 48 | Multi-Environment Cloud-Native App | Resume-level | PLANNED |
| `Projects/46 (InternalDeveloperPlatform)` | `Projects/49-internal-developer-platform` | 46 | 49 | Internal Developer Platform | Resume-level | PLANNED |
| `Projects/49 (DisasterRecoveryBackup)` | `Projects/50-disaster-recovery-and-backup` | 49 | 50 | Disaster Recovery and Backup | Resume-level | PLANNED |

## Special Notes

- WeatherSystem is planned to move from `Projects/06 (WeatherSystem)` to `Projects/30-weather-system-jpa`, as recommended by the audit. Its JPA/Hibernate persistence, H2 database, repository pattern, CSV import, reports/statistics, validation, authorization, and tests make it substantially more advanced than its former position suggests.
- Folder renaming is separate from code cleanup. No Java packages, imports, source files, tests, Maven coordinates, or nested folders will be renamed.
- Generated files, including all existing `target/` directories, will not be removed in this prompt.
- Project README creation or standardization will be handled in later prompts.
- All 50 folders will first move to unique `__tmp_project_XX__` names and then to their final names. No destination will be overwritten or merged.
