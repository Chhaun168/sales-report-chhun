# Quality-attribute scenarios: Angkor Mart monthly sales report

## Stakeholders
- Head-office analyst: Views and exports complete monthly chain and branch reports.
- Branch manager (PNH, REP, BTB): Views branch-specific performance reports and metrics.
- Finance director: Receives automated system notifications when monthly reports are finalized and ready.
- Operations person who runs the job: Schedules, monitors, and troubleshoots daily/monthly batch ingest and report execution.
- Your team as developers: Designs, implements, tests, and maintains the modular Java architecture.

## Scenarios
| ID | Attribute | Source | Stimulus | Artifact | Environment | Response | Response measure | Rank |
|---|---|---|---|---|---|---|---|---|
| QA-1 | Performance | Scheduler | 06:00 on day 2 of M+1 | Report engine | Normal operation, 8-core server, files complete | Report files written, "ready" event published | <= 60 s wall-clock, median of 5 runs after 2 warm-up runs | (H,H) |
| QA-2 | Scalability | Head-office analyst | Adds a 4th branch data source | Report engine | Normal operation | Report complete with new branch data included | Adds at most 35% run time over baseline | (M,M) |
| QA-3 | Availability | Branch router | REP branch file 2 hours late via internet link | Ingest pipeline | Daily batch processing window | Other branch reports out by 07:00; missing branch logged | Others out by 07:00; late file processed on arrival | (H,M) |
| QA-4 | Modifiability | Developer | Request new export format (Excel/PDF layout change) | Report render module | Development environment | New format generated and validated successfully | 2 person-days or less of effort | (H,L) |
| QA-5 | Security | Unauthorized user | Attempts to access/export another branch's data | UI / API layer | Production runtime | Access denied and logged as security audit event | 0 unauthorized leaks; manager sees own branch only | (H,M) |
| QA-6 | Testability | Developer | Runs automated test suite for calculation engine | Calculation engine | Development environment | Test results and coverage report generated | Test suite runs in <= 30 s with no file system or UI dependency | (M,L) |

## Rank justifications
- QA-1 (H,H): The monthly report is strictly due on the morning of the 2nd day, and processing 3 million rows using BigDecimal under tight time limits is computationally heavy.
- QA-2 (M,M): Adding a new branch expands dataset size moderately, requiring scalable collection processing.
- QA-3 (H,M): Network link failures happen occasionally, so the system must handle late-arriving branch data gracefully without failing the entire chain report.
- QA-4 (H,L): Business requirements for reporting formats change often, but a pluggable Strategy pattern keeps modifications isolated.
- QA-5 (H,M): Strict tenant isolation is legally and operationally critical, but standard role-based access control makes it straightforward to enforce.
- QA-6 (M,L): Core calculations must be unit-tested efficiently to ensure financial correctness using BigDecimal.

## Assumptions
- A1: The head-office production server has at least 8 CPU cores and an SSD storage drive.
- A2: Branch POS CSV files arrive over standard internet links by 23:00 daily.
- A3: Authentication tokens reliably identify user roles and permitted branch scopes.

## Utility tree
```mermaid
flowchart LR
    U((Utility)) --> P[Performance]
    U --> S[Scalability]
    U --> A[Availability]
    U --> M[Modifiability]
    U --> Sec[Security]
    U --> T[Testability]

    P --> QA1["QA-1 (H,H) 3 M rows in 60 s or less"]
    S --> QA2["QA-2 (M,M) 4th branch adds <= 35% run time"]
    A --> QA3["QA-3 (H,M) REP file 2 h late: others out by 07:00"]
    M --> QA4["QA-4 (H,L) new export format in 2 days or less"]
    Sec --> QA5["QA-5 (H,M) manager sees own branch only"]
    T --> QA6["QA-6 (M,L) engine tests run in 30 s or less"]