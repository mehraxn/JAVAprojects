# JAVAprojects Repository Audit and Order Plan

Audit date: 2026-07-14

This document is a read-only planning audit of the repository as it exists today. It does not rename, move, delete, build, or rewrite any project. Difficulty and ordering recommendations are based on inspected READMEs, build files, source names/imports, test trees, and infrastructure manifests.

# Repository Structure

```text
JAVAprojects/
├── .agents/
├── LearnigMaterials/
│   ├── README.md
│   ├── 00) START HERE/
│   ├── 1) Java Fundamentals/ ... 20) Exercises/
│   └── topic folders for OOP, collections, testing, JDBC, JPA, CI/CD, etc.
└── Projects/
    ├── 00 (Calculator)/
    ├── 01 (University)/
    ├── ...
    └── 49 (DisasterRecoveryBackup)/
```

- Root folders: `.agents`, `LearnigMaterials`, and `Projects`.
- Root files: none. In particular, the repository root has no `README.md` and no root `.gitignore`.
- `LearnigMaterials` exists and already has a `README.md` plus 21 ordered learning sections (`00` and `1`–`20`).
- `Projects` exists and contains 50 numbered project folders (`00`–`49`).
- No root-level `target/`, compiled classes, logs, IDE folders, or similar generated artifacts were found.
- Every numbered project currently contains at least one project README and one `TEST_RESULTS.md` somewhere in its tree.
- Nine project folders contain at least one `target/`. Five are specifically inside preserved `Raw files` trees; the others are in projects 00, 30, 32, and 33.

# Projects Inventory

`Maven` describes the main implementation, not a preserved raw copy. `Tests` is `PARTIAL` when the project mainly has manifest/configuration validation rather than a conventional unit/integration suite.

| Current # | Folder | Detected project name | Maven | Tests | README | TEST_RESULTS | Generated files | Main topics | Notes |
|---:|---|---|:---:|:---:|:---:|:---:|:---:|---|---|
| 00 | `00 (Calculator)` | Simple Calculator | YES | YES | YES | YES | YES (`target/`) | Java basics, arithmetic, Git lab | Direct project; legacy Maven layout. |
| 01 | `01 (University)` | University Management System | YES | YES | YES | YES | NO | OOP, arrays, registration, exams, ranking, logging | Main code in nested `project/`; preserved `Raw File/`. |
| 02 | `02 (Hydraulic)` | Hydraulic Network Simulator | YES | YES | YES | YES | YES (raw only) | Inheritance, recursion, observer, builder | Main code in `Project/`; preserved `Raw files/`. |
| 03 | `03 (Diet)` | Diet and Takeaway Management | YES | YES | YES | YES | YES (raw only) | OOP domain model, collections, orders | Main code in `Project/`; preserved `Raw files/`. |
| 04 | `04 (MountainHuts)` | Mountain Huts Data Analysis | YES | YES | YES | YES | YES (raw only) | CSV import, streams, grouping, reports | Main code in `project/`; preserved `Raw files/`. |
| 05 | `05 (Social)` | Social Network Backend | YES | YES | YES | YES | YES (raw only) | JPA/Hibernate, H2, repositories, JPQL | Advanced persistence project nested in `project/`. |
| 06 | `06 (WeatherSystem)` | WeatherSystem / Weather Report | YES | YES | YES | YES | YES (raw only) | JPA/Hibernate, H2, repositories, CSV, reports, validation | Large nested `project/`, explanation files, preserved raw tree. |
| 07 | `07 (StudentGradeManager)` | Student Grade Manager | NO | YES | YES | YES | NO | Collections, grades, reports, snapshots | Direct dependency-free `javac` project. |
| 08 | `08 (LibraryManagementSystem)` | Library Management System | NO | YES | YES | YES | NO | Domain lifecycle, dates, loans, snapshots | Direct dependency-free project. |
| 09 | `09 (BankAccountSimulator)` | Bank Account Simulator | NO | YES | YES | YES | NO | `BigDecimal`, transactions, transfers, `Clock` | Direct dependency-free project. |
| 10 | `10 (ParkingGarageSystem)` | Parking Garage System | NO | YES | YES | YES | NO | Multi-entity OOP, billing, time, snapshots | Direct dependency-free project. |
| 11 | `11 (MovieTicketBookingSystem)` | Movie Ticket Booking System | NO | YES | YES | YES | NO | Booking lifecycle, seats, money, time | Direct dependency-free project. |
| 12 | `12 (ProductInventoryManager)` | Product Inventory Manager | NO | YES | YES | YES | NO | Collections, stock rules, sorting, money | Direct dependency-free project. |
| 13 | `13 (TrainTicketReservationSystem)` | Train Ticket Reservation System | NO | YES | YES | YES | NO | Reservations, routes, seats, time | Direct dependency-free project. |
| 14 | `14 (HotelRoomBookingSystem)` | Hotel Room Booking System | NO | YES | YES | YES | NO | Date ranges, rooms, bookings, money | Direct dependency-free project. |
| 15 | `15 (EventRegistrationSystem)` | Event Registration System | NO | YES | YES | YES | NO | Events, attendees, capacity, registration | Direct dependency-free project. |
| 16 | `16 (HospitalQueueManagement)` | Hospital Queue Management | NO | YES | YES | YES | NO | Priority queue, lifecycle, triage | Direct dependency-free project. |
| 17 | `17 (RestaurantOrderingSystem)` | Restaurant Ordering System | NO | YES | YES | YES | NO | Order lifecycle, defensive design, money | Direct dependency-free project. |
| 18 | `18 (QuizExamPlatform)` | Quiz / Exam Platform | NO | YES | YES | YES | NO | Immutable questions, attempts, scoring, ranking | Direct dependency-free project. |
| 19 | `19 (FileBasedAddressBook)` | File-Based Address Book | NO | YES | YES | YES | NO | UTF-8 TSV, CRUD, atomic-style file save | Direct dependency-free project. |
| 20 | `20 (ExpenseTracker)` | Expense Tracker | NO | YES | YES | YES | NO | CSV persistence, `BigDecimal`, reports | Direct dependency-free project. |
| 21 | `21 (TaskManagerJDBC)` | Task Manager JDBC | NO | YES | YES | YES | NO | Repository pattern, JDBC, prepared statements | JDBC compiles without bundled driver; tests use in-memory implementation/static checks. |
| 22 | `22 (URLShortenerBackend)` | URL Shortener Backend | NO | YES | YES | YES | NO | HTTP server, URL validation, CSV, concurrency | Framework-free built-in `HttpServer`. |
| 23 | `23 (ContactsRESTAPI)` | Contacts REST API | NO | YES | YES | YES | NO | REST-style CRUD, HTTP, JSON, pagination | Framework-free, in-memory API. |
| 24 | `24 (BlogAPI)` | Blog API | NO | YES | YES | YES | NO | HTTP API, users/posts/comments, JSON | Framework-free, in-memory API. |
| 25 | `25 (MiniECommerceBackend)` | Mini E-Commerce Backend | NO | YES | YES | YES | NO | Catalog, cart, checkout, stock, money | In-memory service/CLI; no HTTP or database. |
| 26 | `26 (AuthenticationSystem)` | Authentication System | NO | YES | YES | YES | NO | PBKDF2, salts, sessions, roles, secure views | Strong security learning project; in-memory only. |
| 27 | `27 (NotificationService)` | Notification Service | NO | YES | YES | YES | NO | Interfaces, queue, retries, lifecycle, mocks | Local synchronous simulation, no external provider. |
| 28 | `28 (CSVAnalyticsEngine)` | CSV Analytics Engine | NO | YES | YES | YES | NO | CSV parser/writer, grouping, statistics | Quote-aware, dependency-free data project. |
| 29 | `29 (JobApplicationTracker)` | Job Application Tracker | NO | YES | YES | YES | NO | CSV repository, CLI, filters, workflow | Dependency-free local persistence. |
| 30 | `30 (DockerizedJavaPostgres)` | Dockerized Java + PostgreSQL | YES | YES | YES | YES | YES (`target/`) | JDBC, PostgreSQL, Docker, Compose | Runnable one-shot container job. |
| 31 | `31 (CIPipelineJavaApp)` | CI Pipeline Java App | NO | YES | YES | YES | NO | `javac`, tests, packaging, GitHub Actions | Workflow template is nested and needs root wiring to activate. |
| 32 | `32 (TestingCoverageQualityGate)` | Testing Coverage Quality Gate | YES | YES | YES | YES | YES (`target/`) | JUnit 5, Maven, JaCoCo threshold, CI | Enforced coverage quality gate. |
| 33 | `33 (DockerComposeFullStack)` | Docker Compose Full Stack | YES (backend) | PARTIAL | YES | YES | YES (`backend/target`) | Java HTTP, PostgreSQL, Nginx, Compose | Three-service notes stack. |
| 34 | `34 (TerraformInfrastructureStarter)` | Terraform Infrastructure Starter | NO | YES | YES | YES | NO | Terraform modules, validation, native tests | Local-safe `terraform_data` learning environment. |
| 35 | `35 (AnsibleServerConfiguration)` | Ansible Server Configuration | NO | PARTIAL | YES | YES | NO | Inventory, roles, handlers, systemd hardening | Configuration/syntax validation rather than Java tests. |
| 36 | `36 (KubernetesDeploymentJavaApp)` | Kubernetes Deployment Java App | NO | PARTIAL | YES | YES | NO | Java container, Deployment, probes, Kustomize | Complete introductory Kubernetes workload. |
| 37 | `37 (HelmChartJavaApp)` | Helm Chart Java App | NO | PARTIAL | YES | YES | NO | Helm, schema, templates, security defaults | Chart-focused project with render/lint validation. |
| 38 | `38 (PrometheusGrafanaMonitoring)` | Prometheus Grafana Monitoring | NO | PARTIAL | YES | YES | NO | Java metrics, Prometheus, Grafana, Compose | Runnable monitoring path with dashboards/alerts. |
| 39 | `39 (CentralizedLoggingStack)` | Centralized Logging Stack | NO | PARTIAL | YES | YES | NO | Structured JSON, Promtail, Loki, Grafana | Runnable local logging stack. |
| 40 | `40 (GitOpsDeploymentPlatform)` | GitOps Deployment Platform | NO | PARTIAL | YES | YES | NO | Kubernetes, Kustomize, Helm, Argo CD | Desired-state examples; no bundled controller/cluster. |
| 41 | `41 (MicroservicesOrderSystem)` | Microservices Order System | NO | YES | YES | YES | NO | Four Java services, HTTP, Docker Compose, Kubernetes | Includes local integration script and idempotent downstream workflow. |
| 42 | `42 (KubernetesAutoscalingLab)` | Kubernetes Autoscaling Lab | NO | PARTIAL | YES | YES | NO | HPA, metrics-server, resource limits, load testing | Java CPU-load endpoint plus autoscaling manifests. |
| 43 | `43 (SecureCICDPipeline)` | Secure CI/CD Pipeline | NO | YES | YES | YES | NO | gitleaks, Trivy, Syft, Cosign design, Docker | Security-gated CI template plus Java checks. |
| 44 | `44 (FullObservabilityPlatform)` | Full Observability Platform | NO | PARTIAL | YES | YES | NO | Prometheus, Loki, Grafana, trace correlation | Metrics/logs are real; traces are honestly correlation-only. |
| 45 | `45 (InfrastructureAsCodeEnvironment)` | Infrastructure as Code Environment | NO | YES | YES | YES | NO | Terraform, Ansible, environments, handoff | Multi-tool IaC workflow with validation scripts. |
| 46 | `46 (InternalDeveloperPlatform)` | Internal Developer Platform | NO | YES | YES | YES | NO | Golden path, generator, Helm, GitOps examples | Working service generator and validated template. |
| 47 | `47 (BlueGreenCanaryDeployment)` | Blue-Green & Canary Deployment | NO | PARTIAL | YES | YES | NO | Kubernetes, Helm, progressive delivery | Two app versions and deployment/runbook examples. |
| 48 | `48 (MultiEnvironmentCloudNativeApp)` | Multi-Environment Cloud-Native App | NO | PARTIAL | YES | YES | NO | Docker, Helm, Kustomize, GitOps, Terraform | Dev/staging/prod promotion design. |
| 49 | `49 (DisasterRecoveryBackup)` | Disaster Recovery and Backup Lab | NO | YES | YES | YES | NO | PostgreSQL backup/restore, Kubernetes, IaC, monitoring | Locally executable DR workflow with runbooks. |

# Difficulty Assessment

| Current # | Project | Current level | Reason |
|---:|---|---|---|
| 00 | Calculator | Beginner | Minimal arithmetic and introductory Git/Maven workflow. |
| 01 | University | Beginner+ | Single facade, fixed arrays, registrations, averages, ranking, logging. |
| 02 | Hydraulic | Intermediate | Inheritance, recursive graph simulation, observer and fluent builder patterns. |
| 03 | Diet | Intermediate | Larger connected domain model with menus, restaurants, customers, and orders. |
| 04 | Mountain Huts | Intermediate+ | CSV import plus Stream API grouping and statistical reports. |
| 05 | Social | Advanced Java | Real JPA/Hibernate persistence, repositories, relationships, and JPQL feeds. |
| 06 | WeatherSystem | Advanced Java | Large layered JPA application with repositories, CSV import, reports, authorization, validation, and tests. |
| 07 | Student Grade Manager | Beginner+ | Collections, validation, snapshots, summaries, and ranking. |
| 08 | Library Management | Intermediate | Multiple entities, borrowing lifecycle, dates, limits, immutable views. |
| 09 | Bank Account Simulator | Intermediate | `BigDecimal`, atomic transfers, ledgers, deterministic clocks. |
| 10 | Parking Garage | Intermediate | Multi-entity allocation, time-based billing, lifecycle consistency. |
| 11 | Movie Ticket Booking | Intermediate | Atomic seat booking/cancellation, money, time, snapshots. |
| 12 | Product Inventory | Beginner+ | Focused CRUD/stock rules, sorting, `BigDecimal`, defensive views. |
| 13 | Train Reservation | Intermediate | Routes, scheduled services, seats, reservations, cancellation. |
| 14 | Hotel Booking | Beginner+ | Date overlap and booking lifecycle in a compact model. |
| 15 | Event Registration | Beginner+ | Capacity-limited registration and attendee/event relationships. |
| 16 | Hospital Queue | Beginner+ | Priority queues and explicit patient lifecycle transitions. |
| 17 | Restaurant Ordering | Intermediate | Order state machine, discounts, money, defensive snapshots. |
| 18 | Quiz Platform | Intermediate | Immutable question snapshots, attempt locking, grading, scoreboard. |
| 19 | File Address Book | Intermediate | Validated CRUD plus UTF-8 persistence and atomic-style replacement. |
| 20 | Expense Tracker | Intermediate | CSV persistence, quote handling, money, filters, reports. |
| 21 | Task Manager JDBC | Intermediate+ | Repository abstraction and safe JDBC implementation with prepared statements. |
| 22 | URL Shortener | Backend | Thread-safe service, persistence, built-in HTTP server, status/error mapping. |
| 23 | Contacts REST API | Backend | CRUD API, routing, JSON, pagination, synchronization, HTTP tests. |
| 24 | Blog API | Backend | Multi-resource HTTP API, referential rules, cascading cleanup, JSON. |
| 25 | Mini E-Commerce | Intermediate+ | Atomic checkout, carts, stock restoration, order lifecycle, money. |
| 26 | Authentication System | Advanced Java | PBKDF2, salts, constant-time checks, session expiry, roles, secure views. |
| 27 | Notification Service | Intermediate+ | Pluggable interfaces, queue/retry lifecycle, deterministic mock failures. |
| 28 | CSV Analytics | Intermediate+ | Hand-written parser/writer, tabular validation, grouping and numeric statistics. |
| 29 | Job Tracker | Intermediate | Repository/service split and robust CSV workflow. |
| 30 | Dockerized Java + PostgreSQL | Backend | Maven/JDBC application packaged and networked with PostgreSQL via Compose. |
| 31 | CI Pipeline Java App | DevOps | Explicit compile/test/package/artifact CI stages. |
| 32 | Testing Coverage Gate | DevOps | Maven/JUnit/JaCoCo with an enforced quality threshold. |
| 33 | Docker Compose Full Stack | Resume-level | Runnable frontend/backend/database stack with health-gated startup. |
| 34 | Terraform Starter | DevOps | Modules, typed validation, state-safe examples, native Terraform tests. |
| 35 | Ansible Configuration | DevOps | Roles, handlers, variables, permissions, systemd hardening. |
| 36 | Kubernetes Deployment | DevOps | Container security, probes, resources, Service/Deployment, Kustomize. |
| 37 | Helm Chart | DevOps | Parameterized packaging, schema validation, rollout checksums, lint/render workflow. |
| 38 | Prometheus/Grafana | Advanced DevOps | Instrumentation-to-dashboard path with alert rules and Compose. |
| 39 | Centralized Logging | Advanced DevOps | Structured logs through Promtail/Loki into Grafana and LogQL alerts. |
| 40 | GitOps Platform | Advanced DevOps | Kubernetes desired state through Kustomize/Helm and Argo CD examples. |
| 41 | Microservices Order System | Resume-level | Four services, downstream workflow, Compose/Kubernetes, integration testing. |
| 42 | Kubernetes Autoscaling | Advanced DevOps | HPA behavior, metrics dependency, load generation, resource tuning. |
| 43 | Secure CI/CD | Resume-level | Secret, SCA/container, SBOM, and signing stages with least privilege. |
| 44 | Full Observability | Resume-level | Metrics, logs, dashboards, alerts, and honest trace-correlation design. |
| 45 | IaC Environment | Advanced DevOps | Terraform modules, Ansible roles, environment isolation, tool handoff. |
| 46 | Internal Developer Platform | Resume-level | Working self-service generator, golden path, Helm, policies, GitOps examples. |
| 47 | Blue-Green/Canary | Advanced DevOps | Progressive delivery manifests, two versions, Helm and operational runbooks. |
| 48 | Multi-Environment Cloud-Native | Resume-level | Promotion across dev/staging/prod with Docker, Helm, Kustomize, GitOps, Terraform. |
| 49 | Disaster Recovery | Resume-level | Executable backup/restore validation, RPO/RTO thinking, Kubernetes/IaC/runbooks. |

# Current Order Review

The repository already has a recognizable Java → backend → DevOps arc, but projects 05 and 06 create a major early difficulty spike. Several simpler plain-Java and file projects are therefore positioned after substantially harder JPA work. Overall current-order quality: **7/10**.

| Current # | Project | Position judgment | Reason |
|---:|---|---|---|
| 00 | Calculator | GOOD | Correct starting point, but final portfolio numbering should begin at 01. |
| 01 | University | GOOD | Natural first substantial OOP project. |
| 02 | Hydraulic | GOOD | Appropriate early introduction to inheritance and patterns. |
| 03 | Diet | GOOD | Reasonable next larger OOP domain. |
| 04 | Mountain Huts | TOO EARLY | CSV/streams/reporting fits after basic collection and file projects. |
| 05 | Social | TOO EARLY | JPA/Hibernate belongs after JDBC and backend fundamentals. |
| 06 | WeatherSystem | TOO EARLY | Layered JPA/repository/reporting system is far beyond the following beginner projects. |
| 07 | Student Grade Manager | TOO LATE | Should precede the JPA projects. |
| 08 | Library Management | TOO LATE | Solid early/intermediate OOP, not post-JPA. |
| 09 | Bank Account Simulator | TOO LATE | Core Java money/transaction project should be early. |
| 10 | Parking Garage | TOO LATE | Intermediate OOP should precede persistence frameworks. |
| 11 | Movie Ticket Booking | TOO LATE | Intermediate lifecycle project should precede JPA. |
| 12 | Product Inventory | TOO LATE | Focused beginner+ collections project belongs much earlier. |
| 13 | Train Reservation | TOO LATE | Intermediate OOP should precede JPA. |
| 14 | Hotel Booking | TOO LATE | Beginner+ booking rules belong earlier. |
| 15 | Event Registration | TOO LATE | Beginner+ registration model belongs earlier. |
| 16 | Hospital Queue | TOO LATE | Priority-queue exercise belongs before database projects. |
| 17 | Restaurant Ordering | TOO LATE | Intermediate money/state project belongs before JPA. |
| 18 | Quiz Platform | TOO LATE | Intermediate snapshots/scoring belongs before persistence frameworks. |
| 19 | File Address Book | GOOD | Starts the file-persistence portion, though numbering will shift. |
| 20 | Expense Tracker | GOOD | Logical after basic file persistence. |
| 21 | Task Manager JDBC | GOOD | Correct transition into database access. |
| 22 | URL Shortener | GOOD | Appropriate backend/HTTP transition. |
| 23 | Contacts REST API | GOOD | Logical REST-style progression. |
| 24 | Blog API | GOOD | Builds on multi-resource API relationships. |
| 25 | Mini E-Commerce | TOO LATE | It is in-memory intermediate Java, not more advanced than HTTP APIs. |
| 26 | Authentication System | GOOD | Security-focused advanced Java fits near backend work. |
| 27 | Notification Service | TOO LATE | Interface/retry simulation should precede persistence/API projects. |
| 28 | CSV Analytics | TOO LATE | Data/file project should precede JDBC and APIs. |
| 29 | Job Tracker | TOO LATE | CSV CLI/repository project should precede JDBC and APIs. |
| 30 | Dockerized Java + PostgreSQL | GOOD | Strong bridge from backend/database into containers. |
| 31 | CI Pipeline Java App | GOOD | Appropriate start of delivery/tooling projects. |
| 32 | Testing Coverage Gate | NEEDS REVIEW | Quality gates conceptually fit just before or alongside CI. |
| 33 | Docker Compose Full Stack | GOOD | Natural container orchestration step. |
| 34 | Terraform Starter | GOOD | Appropriate infrastructure entry point. |
| 35 | Ansible Configuration | GOOD | Logical configuration-management follow-up. |
| 36 | Kubernetes Deployment | GOOD | Correct first Kubernetes project. |
| 37 | Helm Chart | GOOD | Logical packaging layer after raw manifests. |
| 38 | Prometheus/Grafana | GOOD | Monitoring follows deployable workloads. |
| 39 | Centralized Logging | GOOD | Complements metrics before full observability. |
| 40 | GitOps Platform | GOOD | Appropriate advanced deployment workflow. |
| 41 | Microservices Order System | GOOD | Strong late-stage distributed application. |
| 42 | Kubernetes Autoscaling | GOOD | Advanced cluster behavior after deployment basics. |
| 43 | Secure CI/CD | GOOD | Advanced delivery/security topic. |
| 44 | Full Observability | GOOD | Integrates earlier metrics/logging concepts. |
| 45 | IaC Environment | NEEDS REVIEW | Could precede some platform capstones, but current late position is defensible. |
| 46 | Internal Developer Platform | GOOD | Strong capstone-level platform project. |
| 47 | Blue-Green/Canary | GOOD | Advanced operational delivery topic. |
| 48 | Multi-Environment Cloud-Native | GOOD | Near-capstone integration across environments. |
| 49 | Disaster Recovery | GOOD | Appropriate final operational-resilience capstone. |

# Recommended Final Order

This plan converts the current `00`–`49` sequence into professional `01`–`50` lowercase kebab-case names. It is only a recommendation; no folders are renamed in this prompt.

| New # | Project | Old # | New folder name suggestion | Level | Main topics | Reason |
|---:|---|---:|---|---|---|---|
| 01 | Simple Calculator | 00 | `01-simple-calculator` | Beginner | Java basics, Git, Maven | Smallest entry project. |
| 02 | University Management | 01 | `02-university-management` | Beginner+ | OOP, arrays, registration, averages | First complete domain application. |
| 03 | Hydraulic Network Simulator | 02 | `03-hydraulic-network-simulator` | Intermediate | Inheritance, recursion, observer, builder | Introduces polymorphism and patterns. |
| 04 | Diet and Takeaway Management | 03 | `04-diet-takeaway-management` | Intermediate | Rich OOP domain, collections, orders | Expands object relationships and workflows. |
| 05 | Student Grade Manager | 07 | `05-student-grade-manager` | Beginner+ | Collections, summaries, ranking | Focused collections/reporting practice. |
| 06 | Product Inventory Manager | 12 | `06-product-inventory-manager` | Beginner+ | Stock, sorting, `BigDecimal` | Compact service-layer project. |
| 07 | Bank Account Simulator | 09 | `07-bank-account-simulator` | Intermediate | Money, transactions, atomic transfer | Adds correctness-sensitive business rules. |
| 08 | Library Management System | 08 | `08-library-management-system` | Intermediate | Loans, dates, lifecycle, limits | Multi-entity state consistency. |
| 09 | Hotel Room Booking | 14 | `09-hotel-room-booking` | Beginner+ | Dates, overlap, bookings | Focused date-range rules. |
| 10 | Event Registration | 15 | `10-event-registration-system` | Beginner+ | Capacity, attendee registration | Simple relationship management. |
| 11 | Hospital Queue Management | 16 | `11-hospital-queue-management` | Beginner+ | Priority queue, lifecycle | Introduces priority-based structures. |
| 12 | Restaurant Ordering | 17 | `12-restaurant-ordering-system` | Intermediate | State machine, money, discounts | Stronger lifecycle and defensive design. |
| 13 | Quiz / Exam Platform | 18 | `13-quiz-exam-platform` | Intermediate | Snapshots, grading, scoreboard | Immutable state and result ranking. |
| 14 | Parking Garage | 10 | `14-parking-garage-system` | Intermediate | Allocation, time billing, snapshots | Broader entity coordination. |
| 15 | Movie Ticket Booking | 11 | `15-movie-ticket-booking-system` | Intermediate | Atomic booking, seats, money | Transaction-like in-memory workflow. |
| 16 | Train Ticket Reservation | 13 | `16-train-ticket-reservation-system` | Intermediate | Routes, seats, cancellation | Larger reservation model. |
| 17 | Mini E-Commerce Backend | 25 | `17-mini-ecommerce-backend` | Intermediate+ | Cart, checkout, stock, orders | Culminating in-memory business workflow. |
| 18 | Notification Service | 27 | `18-notification-service` | Intermediate+ | Interfaces, queues, retries, mocks | Adds pluggability and failure handling. |
| 19 | File-Based Address Book | 19 | `19-file-based-address-book` | Intermediate | Files, CRUD, safe replacement | First persistence-by-file project. |
| 20 | Expense Tracker | 20 | `20-expense-tracker` | Intermediate | CSV, money, reports | Adds structured file persistence. |
| 21 | Job Application Tracker | 29 | `21-job-application-tracker` | Intermediate | CSV repository, CLI, filters | Reinforces repository/service separation. |
| 22 | CSV Analytics Engine | 28 | `22-csv-analytics-engine` | Intermediate+ | Parsing, grouping, statistics | Most technically substantial file/data project. |
| 23 | Mountain Huts Data Analysis | 04 | `23-mountain-huts-data-analysis` | Intermediate+ | CSV import, streams, reports | Bridges file data into advanced Java queries. |
| 24 | Task Manager JDBC | 21 | `24-task-manager-jdbc` | Intermediate+ | JDBC, prepared statements, repositories | First relational-database boundary. |
| 25 | URL Shortener Backend | 22 | `25-url-shortener-backend` | Backend | HTTP, concurrency, CSV, validation | First substantial framework-free server. |
| 26 | Contacts REST API | 23 | `26-contacts-rest-api` | Backend | REST CRUD, JSON, pagination | Clear API fundamentals. |
| 27 | Blog API | 24 | `27-blog-api` | Backend | Multi-resource API, relationships | More connected API domain. |
| 28 | Authentication System | 26 | `28-authentication-system` | Advanced Java | PBKDF2, sessions, roles | Security-intensive application logic. |
| 29 | Social Network JPA | 05 | `29-social-network-jpa` | Advanced Java | JPA/Hibernate, repositories, JPQL | First ORM-backed domain project. |
| 30 | Weather System JPA | 06 | `30-weather-system-jpa` | Advanced Java | Layered JPA, CSV, reports, authorization | Most comprehensive Java/persistence project. |
| 31 | Testing Coverage Quality Gate | 32 | `31-testing-coverage-quality-gate` | DevOps | JUnit, Maven, JaCoCo gate | Establishes automated quality policy before delivery. |
| 32 | CI Pipeline Java App | 31 | `32-ci-pipeline-java-app` | DevOps | Compile, test, package, artifacts | Introduces CI stages. |
| 33 | Dockerized Java PostgreSQL | 30 | `33-dockerized-java-postgresql` | Backend | JDBC, PostgreSQL, Docker, Compose | Bridges application delivery and containers. |
| 34 | Docker Compose Full Stack | 33 | `34-docker-compose-full-stack` | Resume-level | Nginx, Java, PostgreSQL, Compose | Complete multi-container stack. |
| 35 | Terraform Infrastructure Starter | 34 | `35-terraform-infrastructure-starter` | DevOps | Terraform modules, validation, tests | First infrastructure-as-code project. |
| 36 | Ansible Server Configuration | 35 | `36-ansible-server-configuration` | DevOps | Roles, handlers, systemd | Adds configuration management. |
| 37 | Kubernetes Deployment Java App | 36 | `37-kubernetes-java-deployment` | DevOps | Deployment, Service, probes, Kustomize | Kubernetes fundamentals. |
| 38 | Helm Chart Java App | 37 | `38-helm-chart-java-app` | DevOps | Helm, schema, templates | Packages Kubernetes workloads safely. |
| 39 | Prometheus Grafana Monitoring | 38 | `39-prometheus-grafana-monitoring` | Advanced DevOps | Metrics, alerts, dashboards | Introduces production monitoring flow. |
| 40 | Centralized Logging Stack | 39 | `40-centralized-logging-stack` | Advanced DevOps | JSON logs, Promtail, Loki, Grafana | Adds centralized logs. |
| 41 | Microservices Order System | 41 | `41-microservices-order-system` | Resume-level | Services, HTTP, Compose, Kubernetes | Strong distributed-system application. |
| 42 | GitOps Deployment Platform | 40 | `42-gitops-deployment-platform` | Advanced DevOps | Kustomize, Helm, Argo CD | Applies Git-driven desired state. |
| 43 | Kubernetes Autoscaling Lab | 42 | `43-kubernetes-autoscaling-lab` | Advanced DevOps | HPA, metrics, load, resources | Adds dynamic scaling and capacity behavior. |
| 44 | Secure CI/CD Pipeline | 43 | `44-secure-ci-cd-pipeline` | Resume-level | Scanning, SBOM, signing, least privilege | Adds supply-chain security gates. |
| 45 | Infrastructure as Code Environment | 45 | `45-infrastructure-as-code-environment` | Advanced DevOps | Terraform, Ansible, environments | Integrates provisioning and configuration. |
| 46 | Full Observability Platform | 44 | `46-full-observability-platform` | Resume-level | Metrics, logs, dashboards, correlation | Integrates observability pillars honestly. |
| 47 | Blue-Green and Canary Deployment | 47 | `47-blue-green-canary-deployment` | Advanced DevOps | Progressive delivery, Helm, runbooks | Advanced rollout operations. |
| 48 | Multi-Environment Cloud-Native App | 48 | `48-multi-environment-cloud-native-app` | Resume-level | Helm, Kustomize, GitOps, Terraform | Integrates promotion across environments. |
| 49 | Internal Developer Platform | 46 | `49-internal-developer-platform` | Resume-level | Golden path, generator, policies, GitOps | Platform-engineering capstone. |
| 50 | Disaster Recovery and Backup | 49 | `50-disaster-recovery-and-backup` | Resume-level | PostgreSQL DR, restore validation, RPO/RTO, IaC | Final operational-resilience capstone. |

# Featured Projects

The root README should feature 8–10 strong, distinct projects rather than only the final numbered entries.

| Featured project | Why it should be featured | Main technologies |
|---|---|---|
| Weather System JPA | Strongest pure-Java architecture: layered operations, ORM repositories, imports, reports, validation, and extensive tests. | Java 21, Maven, JPA/Hibernate, H2, JPQL, CSV |
| Social Network JPA | Demonstrates ORM relationships, repository pattern, bidirectional domain rules, feeds, and pagination. | Java 21, JPA/Hibernate, H2, JPQL, Maven |
| Docker Compose Full Stack | A runnable application stack showing frontend, backend, database, healthchecks, and persistence. | Java, HTTP, PostgreSQL, Nginx, Docker Compose |
| Microservices Order System | Shows service boundaries, downstream workflows, integration testing, and container/Kubernetes deployment. | Java HTTP services, Docker Compose, Kubernetes |
| Secure CI/CD Pipeline | Strong software-supply-chain story with explicit security gates and artifact evidence. | GitHub Actions, gitleaks, Trivy, Syft, Cosign, Docker |
| Full Observability Platform | Integrates metrics, logs, dashboards, alerts, and honest trace correlation. | Prometheus, Loki, Promtail, Grafana, Docker Compose |
| Infrastructure as Code Environment | Shows provisioning/configuration separation and multi-environment automation. | Terraform, Ansible, scripts, state/security practices |
| Internal Developer Platform | A working golden-path generator is a compelling platform-engineering portfolio artifact. | Java, shell, Helm, Kubernetes, GitOps, policies |
| Multi-Environment Cloud-Native App | Demonstrates promotion and environment-specific desired state across several delivery tools. | Docker, Helm, Kustomize, Argo CD concepts, Terraform |
| Disaster Recovery and Backup | Provides a locally executable restore story and operational runbooks rather than diagrams alone. | PostgreSQL, Docker Compose, Kubernetes, Terraform, Ansible, monitoring |

Top five for the most prominent root-README cards: **Weather System JPA, Microservices Order System, Secure CI/CD Pipeline, Full Observability Platform, and Internal Developer Platform**.

# README Strategy

## 1. Root README purpose

The root README should act as the portfolio landing page, not as a duplicate of every project document. It should explain the Java-to-DevOps learning path, surface the best work quickly, and provide stable links into the final renamed folders.

Recommended sections:

1. Title and concise portfolio statement.
2. Overview and what the repository demonstrates.
3. Learning path (Java fundamentals → OOP → files/data → JDBC/JPA/backend → containers/CI → Kubernetes/platform engineering).
4. Featured projects with short outcome-focused descriptions.
5. Full 01–50 project table with level, key technologies, and link.
6. Technology coverage summary.
7. Repository structure.
8. General run/test guidance, pointing to project-specific commands.
9. Honest status notes (framework-free projects, example-only infrastructure, generated files not committed).
10. Optional contact/profile note supplied by the owner; do not invent personal links.

## 2. Project README purpose

Each project README should be independently useful to a reviewer who lands directly on that folder. Use a consistent template while retaining project-specific architecture and exact commands:

- Project title and one-paragraph overview.
- Implemented features (not aspirations).
- Tech stack and requirements.
- What the project demonstrates.
- Architecture/design and important behavior.
- How to run and how to test.
- Small project-structure tree.
- Known limitations and which parts are examples only.
- Resume-value sentence.
- Links to `TEST_RESULTS.md`, `docs/`, and scripts when present.

## 3. `LearnigMaterials` README purpose

The existing README should remain a learning-map/index: topic sequence, prerequisites, how materials relate to projects, and navigation links. A later prompt may correct the folder spelling only as a coordinated rename; do not duplicate full tutorials in the root portfolio README.

## 4. Content ownership and duplication rules

- Root README: breadth, navigation, progression, highlights.
- Project README: depth, behavior, local commands, architecture, limitations.
- `TEST_RESULTS.md`: actual dated evidence and environment details.
- `docs/`: extended architecture, testing, runbooks, or design decisions.
- Learning README: curriculum navigation.

Avoid repeating long feature lists, full command matrices, or test evidence in the root README. The root table should link to the authoritative project README. Keep generated test counts in `TEST_RESULTS.md`, because they change more often than portfolio summaries.

## 5. Rollout strategy for later prompts

1. Finalize and execute the rename map atomically.
2. Fix every internal/root link after renaming.
3. Create the root README only against final paths.
4. Standardize early-project READMEs without rewriting working code.
5. Standardize intermediate/backend/database READMEs and clearly distinguish framework-free HTTP from framework-based backend work.
6. Standardize DevOps READMEs, explicitly separating runnable validation from example-only cluster/cloud operations.

# Cleanup Risks and Warnings

- Folder renames will break README links, scripts, workflow `working-directory` values, Maven/module references, diagrams, and documented commands unless updated in one coordinated change.
- Renumbering 50 folders should use a collision-safe two-phase rename (temporary unique names, then final names); direct sequential renaming can collide with existing numbers.
- Projects 01–06 contain nested `project`/`Project` and preserved `Raw files` trees. A later cleanup must define the canonical implementation and must not silently delete raw course material.
- Moving WeatherSystem changes all later numbering and any links that embed `06 (WeatherSystem)`.
- GitHub only activates workflows under the repository-root `.github/workflows`; nested project workflows are documentation/templates unless a root workflow calls them.
- Generated `target/` folders should be removed only in an explicit cleanup prompt after checking whether they are in canonical or preserved raw trees.
- Do not globally replace Java 25 with Java 21 without inspecting each build; handle compatibility per project and run its real tests.
- Do not convert all dependency-free `javac` projects to Maven merely for visual consistency. Their zero-dependency build is part of their learning story unless a later decision explicitly changes it.
- Do not label built-in `HttpServer` projects as Spring Boot or imply databases where storage is in memory/CSV.
- Do not claim Kubernetes, Argo CD, Terraform, cloud, or signing operations were executed when a project only validates manifests or supplies examples. Preserve the existing honest distinctions.
- Professor/base tests and public APIs in the original OOP labs must remain compatible during future cleanup.
- Correcting `LearnigMaterials` to `LearningMaterials` is desirable, but it is a repository-wide link change and should be done only with the controlled rename prompt.
- Root README featured-project claims should cite implemented behavior and recorded validation, not planned improvements.
- Never commit `target/`, real secrets, Terraform state, real `.tfvars`, database volumes, backup archives, or local generated reports.

# Recommended Next Prompts

## Prompt 2 — Rename and reorder project folders safely

- **Goal:** Apply the approved 01–50 map with lowercase kebab-case names, optionally correct `LearnigMaterials`, and repair all path references.
- **May modify:** Folder names, links, workflow paths, script working directories, documentation paths, and repository navigation references.
- **Must not modify:** Project business logic, professor/base tests, raw learning content, test results, or generated artifacts except where explicitly authorized.
- **Risks:** Rename collisions, broken links, case-only renames on Windows, nested `project`/`Project` ambiguity, and Git history noise. Use temporary names and verify every link/path afterward.

## Prompt 3 — Create the root portfolio README

- **Goal:** Create the root `README.md` using the final folder names, learning path, featured projects, complete project table, technology coverage, and honest status notes.
- **May modify:** Root `README.md` only, plus root-local image assets if explicitly approved.
- **Must not modify:** Project code, project READMEs, build files, tests, or folder names.
- **Risks:** Links must use the post-rename paths; avoid invented test results, inflated technology claims, and excessive duplication.

## Prompt 4 — Improve READMEs for early Java projects

- **Goal:** Standardize projects 01–18 (calculator through notification service) with concise, evidence-based project documentation.
- **May modify:** Each selected project's `README.md` and project-local docs when necessary.
- **Must not modify:** Code, tests, public APIs, build configuration, raw course folders, or unrelated projects.
- **Risks:** Do not flatten meaningful project differences or claim Maven/framework use for dependency-free projects.

## Prompt 5 — Improve READMEs for intermediate/backend/database projects

- **Goal:** Standardize projects 19–34, including file/CSV, JDBC, framework-free APIs, authentication, JPA, WeatherSystem, quality/CI, and containerized backend projects.
- **May modify:** Selected project READMEs and documentation links.
- **Must not modify:** Application code, database schemas, tests, wrapper/build files, or runtime manifests unless separately requested.
- **Risks:** Clearly distinguish in-memory, CSV, JDBC, JPA, and real containerized database behavior; retain honest limitations and test evidence.

## Prompt 6 — Improve DevOps READMEs and run final consistency review

- **Goal:** Standardize projects 35–50, then verify root/project links, terminology, runnable-vs-example claims, folder order, and generated-file policy.
- **May modify:** DevOps project READMEs/docs and root navigation corrections discovered during consistency checking.
- **Must not modify:** Infrastructure behavior, secrets, application code, test evidence, or deployment state unless explicitly authorized.
- **Risks:** Do not imply real cloud/cluster deployment where only lint/render/static validation exists; keep placeholders and secret guidance safe.

# Rename Execution Status

The recommended folder rename/reorder operation was executed in Prompt 2.
See `REPO_RENAME_EXECUTION_REPORT.md` for the final mapping.
