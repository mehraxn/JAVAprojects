# Java Projects Portfolio: From OOP to DevOps

## Overview

This repository is a structured portfolio of 50 Java and DevOps projects, ordered from introductory programming exercises to backend systems, persistence, automated delivery, cloud-native operations, and platform engineering. Each project focuses on a defined set of skills and links to its own documentation and available validation evidence.

The collection is being progressively refined as a portfolio. Project scope varies intentionally: some entries are dependency-free Java applications, some use Maven or databases, and later entries focus on deployment and operational workflows rather than application size.

## What This Repository Demonstrates

- Java fundamentals, object-oriented design, collections, streams, and domain modeling
- File and CSV processing, reporting, validation, and defensive programming
- Unit testing, Maven builds, JUnit, and JaCoCo quality gates
- JDBC, PostgreSQL, JPA/Hibernate, H2, repository patterns, and JPQL
- Framework-free HTTP services, REST-style APIs, JSON, authentication, and microservices
- GitHub Actions, Docker, Docker Compose, Kubernetes, Helm, and GitOps
- Terraform, Ansible, monitoring, centralized logging, and observability
- Secure delivery, progressive deployment, platform engineering, and disaster recovery

## Learning Path

1. **Beginner Java and OOP** — syntax, classes, collections, validation, and small domain models.
2. **Intermediate domain workflows** — lifecycle rules, money, time, reservations, state, and larger object relationships.
3. **Files, data, and persistence** — CSV parsing, analytics, repositories, JDBC, and ORM-backed applications.
4. **Backend development** — HTTP services, REST-style APIs, security, PostgreSQL, and multi-service systems.
5. **Delivery and cloud-native foundations** — testing gates, CI/CD, Docker, Kubernetes, Helm, and GitOps.
6. **Advanced DevOps and portfolio projects** — observability, infrastructure automation, progressive delivery, internal platforms, and recovery workflows.

## Featured Projects

| Project | Why It Is Featured | Main Technologies |
|---|---|---|
| [Weather System JPA](Projects/30-weather-system-jpa) | A comprehensive layered Java application with persistence, imports, validation, authorization, reports, and tests. | Java 21, Maven, JPA/Hibernate, H2, JPQL, CSV |
| [Social Network JPA](Projects/29-social-network-jpa) | Demonstrates ORM relationships, repositories, domain rules, feeds, and pagination. | Java 21, Maven, JPA/Hibernate, H2, JPQL |
| [Docker Compose Full Stack](Projects/34-docker-compose-full-stack) | Connects a frontend, Java backend, and PostgreSQL database with health-aware container orchestration. | Java, PostgreSQL, Nginx, Docker Compose |
| [Microservices Order System](Projects/41-microservices-order-system) | Explores service boundaries, HTTP workflows, integration validation, and deployment manifests. | Java HTTP services, Docker Compose, Kubernetes |
| [Secure CI/CD Pipeline](Projects/44-secure-ci-cd-pipeline) | Models security gates, artifact evidence, SBOM generation, scanning, and signing design. | GitHub Actions, gitleaks, Trivy, Syft, Cosign, Docker |
| [Full Observability Platform](Projects/46-full-observability-platform) | Brings together metrics, structured logs, dashboards, alerts, and trace correlation. | Prometheus, Loki, Promtail, Grafana, Docker Compose |
| [Internal Developer Platform](Projects/49-internal-developer-platform) | Provides a working golden-path service generator with deployment and policy examples. | Java, Helm, Kubernetes, GitOps, policy templates |
| [Disaster Recovery and Backup](Projects/50-disaster-recovery-and-backup) | Presents executable backup/restore workflows and operational recovery guidance. | PostgreSQL, Docker Compose, Kubernetes, Terraform, Ansible |

## Full Project List

| # | Project | Level | Main Topics | Folder |
|---:|---|---|---|---|
| 01 | Simple Calculator | Beginner | Java basics, Git, Maven | [Projects/01-simple-calculator](Projects/01-simple-calculator) |
| 02 | University Management | Beginner+ | OOP, arrays, registration, averages | [Projects/02-university-management](Projects/02-university-management) |
| 03 | Hydraulic Network Simulator | Intermediate | Inheritance, recursion, observer, builder | [Projects/03-hydraulic-network-simulator](Projects/03-hydraulic-network-simulator) |
| 04 | Diet and Takeaway Management | Intermediate | Domain modeling, collections, orders | [Projects/04-diet-takeaway-management](Projects/04-diet-takeaway-management) |
| 05 | Student Grade Manager | Beginner+ | Collections, summaries, ranking | [Projects/05-student-grade-manager](Projects/05-student-grade-manager) |
| 06 | Product Inventory Manager | Beginner+ | Stock, sorting, `BigDecimal` | [Projects/06-product-inventory-manager](Projects/06-product-inventory-manager) |
| 07 | Bank Account Simulator | Intermediate | Money, transactions, atomic transfer | [Projects/07-bank-account-simulator](Projects/07-bank-account-simulator) |
| 08 | Library Management System | Intermediate | Loans, dates, lifecycle, limits | [Projects/08-library-management-system](Projects/08-library-management-system) |
| 09 | Hotel Room Booking | Beginner+ | Dates, overlap, bookings | [Projects/09-hotel-room-booking](Projects/09-hotel-room-booking) |
| 10 | Event Registration | Beginner+ | Capacity, attendee registration | [Projects/10-event-registration-system](Projects/10-event-registration-system) |
| 11 | Hospital Queue Management | Beginner+ | Priority queues, lifecycle | [Projects/11-hospital-queue-management](Projects/11-hospital-queue-management) |
| 12 | Restaurant Ordering | Intermediate | State, money, discounts | [Projects/12-restaurant-ordering-system](Projects/12-restaurant-ordering-system) |
| 13 | Quiz / Exam Platform | Intermediate | Snapshots, grading, ranking | [Projects/13-quiz-exam-platform](Projects/13-quiz-exam-platform) |
| 14 | Parking Garage | Intermediate | Allocation, time billing, snapshots | [Projects/14-parking-garage-system](Projects/14-parking-garage-system) |
| 15 | Movie Ticket Booking | Intermediate | Atomic booking, seats, money | [Projects/15-movie-ticket-booking-system](Projects/15-movie-ticket-booking-system) |
| 16 | Train Ticket Reservation | Intermediate | Routes, seats, cancellation | [Projects/16-train-ticket-reservation-system](Projects/16-train-ticket-reservation-system) |
| 17 | Mini E-Commerce Backend | Intermediate+ | Cart, checkout, stock, orders | [Projects/17-mini-ecommerce-backend](Projects/17-mini-ecommerce-backend) |
| 18 | Notification Service | Intermediate+ | Interfaces, queues, retries, mocks | [Projects/18-notification-service](Projects/18-notification-service) |
| 19 | File-Based Address Book | Intermediate | Files, CRUD, safe replacement | [Projects/19-file-based-address-book](Projects/19-file-based-address-book) |
| 20 | Expense Tracker | Intermediate | CSV, money, reports | [Projects/20-expense-tracker](Projects/20-expense-tracker) |
| 21 | Job Application Tracker | Intermediate | CSV repository, CLI, filters | [Projects/21-job-application-tracker](Projects/21-job-application-tracker) |
| 22 | CSV Analytics Engine | Intermediate+ | Parsing, grouping, statistics | [Projects/22-csv-analytics-engine](Projects/22-csv-analytics-engine) |
| 23 | Mountain Huts Data Analysis | Intermediate+ | CSV import, streams, reports | [Projects/23-mountain-huts-data-analysis](Projects/23-mountain-huts-data-analysis) |
| 24 | Task Manager JDBC | Intermediate+ | JDBC, prepared statements, repositories | [Projects/24-task-manager-jdbc](Projects/24-task-manager-jdbc) |
| 25 | URL Shortener Backend | Backend | HTTP, concurrency, CSV, validation | [Projects/25-url-shortener-backend](Projects/25-url-shortener-backend) |
| 26 | Contacts REST API | Backend | REST-style CRUD, JSON, pagination | [Projects/26-contacts-rest-api](Projects/26-contacts-rest-api) |
| 27 | Blog API | Backend | HTTP API, relationships, JSON | [Projects/27-blog-api](Projects/27-blog-api) |
| 28 | Authentication System | Advanced Java | PBKDF2, sessions, roles | [Projects/28-authentication-system](Projects/28-authentication-system) |
| 29 | Social Network JPA | Advanced Java | JPA/Hibernate, repositories, JPQL | [Projects/29-social-network-jpa](Projects/29-social-network-jpa) |
| 30 | Weather System JPA | Advanced Java | Layered JPA, CSV, reports, authorization | [Projects/30-weather-system-jpa](Projects/30-weather-system-jpa) |
| 31 | Testing Coverage Quality Gate | DevOps | JUnit, Maven, JaCoCo gate | [Projects/31-testing-coverage-quality-gate](Projects/31-testing-coverage-quality-gate) |
| 32 | CI Pipeline Java App | DevOps | Compile, test, package, artifacts | [Projects/32-ci-pipeline-java-app](Projects/32-ci-pipeline-java-app) |
| 33 | Dockerized Java PostgreSQL | Backend | JDBC, PostgreSQL, Docker Compose | [Projects/33-dockerized-java-postgresql](Projects/33-dockerized-java-postgresql) |
| 34 | Docker Compose Full Stack | Resume-level | Nginx, Java, PostgreSQL, Compose | [Projects/34-docker-compose-full-stack](Projects/34-docker-compose-full-stack) |
| 35 | Terraform Infrastructure Starter | DevOps | Terraform modules, validation, tests | [Projects/35-terraform-infrastructure-starter](Projects/35-terraform-infrastructure-starter) |
| 36 | Ansible Server Configuration | DevOps | Roles, handlers, systemd | [Projects/36-ansible-server-configuration](Projects/36-ansible-server-configuration) |
| 37 | Kubernetes Deployment Java App | DevOps | Deployments, Services, probes, Kustomize | [Projects/37-kubernetes-java-deployment](Projects/37-kubernetes-java-deployment) |
| 38 | Helm Chart Java App | DevOps | Helm, schemas, templates | [Projects/38-helm-chart-java-app](Projects/38-helm-chart-java-app) |
| 39 | Prometheus Grafana Monitoring | Advanced DevOps | Metrics, alerts, dashboards | [Projects/39-prometheus-grafana-monitoring](Projects/39-prometheus-grafana-monitoring) |
| 40 | Centralized Logging Stack | Advanced DevOps | JSON logs, Promtail, Loki, Grafana | [Projects/40-centralized-logging-stack](Projects/40-centralized-logging-stack) |
| 41 | Microservices Order System | Resume-level | Services, HTTP, Compose, Kubernetes | [Projects/41-microservices-order-system](Projects/41-microservices-order-system) |
| 42 | GitOps Deployment Platform | Advanced DevOps | Kustomize, Helm, Argo CD | [Projects/42-gitops-deployment-platform](Projects/42-gitops-deployment-platform) |
| 43 | Kubernetes Autoscaling Lab | Advanced DevOps | HPA, metrics, load, resources | [Projects/43-kubernetes-autoscaling-lab](Projects/43-kubernetes-autoscaling-lab) |
| 44 | Secure CI/CD Pipeline | Resume-level | Scanning, SBOM, signing, least privilege | [Projects/44-secure-ci-cd-pipeline](Projects/44-secure-ci-cd-pipeline) |
| 45 | Infrastructure as Code Environment | Advanced DevOps | Terraform, Ansible, environments | [Projects/45-infrastructure-as-code-environment](Projects/45-infrastructure-as-code-environment) |
| 46 | Full Observability Platform | Resume-level | Metrics, logs, dashboards, correlation | [Projects/46-full-observability-platform](Projects/46-full-observability-platform) |
| 47 | Blue-Green and Canary Deployment | Advanced DevOps | Progressive delivery, Helm, runbooks | [Projects/47-blue-green-canary-deployment](Projects/47-blue-green-canary-deployment) |
| 48 | Multi-Environment Cloud-Native App | Resume-level | Helm, Kustomize, GitOps, Terraform | [Projects/48-multi-environment-cloud-native-app](Projects/48-multi-environment-cloud-native-app) |
| 49 | Internal Developer Platform | Resume-level | Golden path, generator, policies, GitOps | [Projects/49-internal-developer-platform](Projects/49-internal-developer-platform) |
| 50 | Disaster Recovery and Backup | Resume-level | PostgreSQL DR, restore validation, RPO/RTO | [Projects/50-disaster-recovery-and-backup](Projects/50-disaster-recovery-and-backup) |

## Technologies Covered

**Languages, build, and testing:** Java, Maven, JUnit 5, JaCoCo, `javac`, shell and PowerShell scripts

**Backend and persistence:** Java HTTP server, REST-style APIs, JSON, JDBC, PostgreSQL, JPA/Hibernate, H2, CSV

**Delivery and infrastructure:** GitHub Actions, Docker, Docker Compose, Kubernetes, Kustomize, Helm, Argo CD examples, Terraform, Ansible

**Operations and security:** Prometheus, Grafana, Loki, Promtail, gitleaks, Trivy, Syft, Cosign workflow design

## Repository Structure

```text
JAVAprojects/
├── LearnigMaterials/                  # Supporting notes and revision material
├── Projects/
│   ├── 01-simple-calculator/
│   ├── 02-university-management/
│   ├── ...
│   └── 50-disaster-recovery-and-backup/
├── REPO_AUDIT_AND_ORDER_PLAN.md
├── REPO_RENAME_EXECUTION_REPORT.md
└── README.md
```

## How to Use This Repository

Open [`Projects/`](Projects) and select a project at the appropriate level. Each project is self-contained; consult its local README for exact requirements, structure, run commands, limitations, and available test evidence. Some early coursework projects retain nested canonical and raw-material folders, so project-specific instructions take precedence over repository-wide conventions.

## Running Projects

For a Maven project, enter the directory containing its `pom.xml` and use the available Maven wrapper when present:

```bash
./mvnw clean test
```

On Windows, the wrapper command is usually `mvnw.cmd clean test`. If no wrapper is included, use:

```bash
mvn clean test
```

For a dependency-free Java project, follow its README or supplied scripts; projects without Maven can generally be compiled with `javac`. Container, Kubernetes, and infrastructure projects have their own prerequisites and validation instructions. A command shown here is general guidance, not a claim that every project uses the same build or has passed the same checks.

## Learning Materials

[`LearnigMaterials/`](LearnigMaterials) contains supporting Java notes and revision material used alongside the projects. Its [learning-materials index](LearnigMaterials/README.md) provides topic-based navigation. The existing folder spelling is retained intentionally to avoid an uncoordinated repository-wide rename.

## Current Status

This repository is actively being cleaned and documented as a portfolio. The project ordering and root navigation are established, and every project currently contains local documentation and recorded test or validation notes. The depth of automation and runtime validation varies by project: some are polished runnable applications, while some DevOps entries are intentionally local-safe templates or manifest-validation exercises rather than evidence of a live cloud deployment.

## Suggested Resume Summary

Structured and maintained a 50-project Java portfolio progressing from OOP fundamentals through backend development, persistence, automated testing, CI/CD, containers, Kubernetes, observability, and infrastructure automation.

## Suggested Repository Topics

`java` · `oop` · `maven` · `junit` · `backend` · `hibernate` · `docker` · `kubernetes` · `devops` · `ci-cd` · `portfolio`

## License

License information has not been added yet.
