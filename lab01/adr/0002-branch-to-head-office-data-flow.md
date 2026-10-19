# ADR-0002: Batch SFTP upload with idempotent naming and partial-report policy

Status: Accepted
Date: 2026-10-19
Deciders: Chum Kimchhun

## Context
Forces that make this decision necessary: each of the three branches (PNH, REP, BTB) must transmit daily transaction CSV files to head office reliably (QA-3), network connectivity can fail intermittently, and reports must gracefully handle missing branch data by 08:00.

## Decision
We will use scheduled daily SFTP batch file uploads where each branch pushes one file per day (`BRANCH-yyyy-MM-dd.csv`). Head office processes available files at report generation time; if a branch file is missing due to a link failure at 23:30, head office publishes a partial report excluding that branch, logs a warning for the analyst, and automatically merges the late file idempotently upon subsequent re-runs.

## Alternatives considered
- Real-time message broker streaming per receipt: offers instant updates, but fails completely if branch network links drop and requires complex broker infrastructure.
- Head-office pull via scheduled polling: tightly couples head office to branch internal network states and increases firewall/security configuration complexity at remote branches.

## Consequences
+ Idempotent file naming (`BRANCH-yyyy-MM-dd.csv`) ensures re-running the loader never creates duplicate transaction records (QA-3).
+ Graceful handling of network drops allows morning reports to proceed without total system blockage.
- Reports generated before a late branch file arrives are incomplete until a manual or scheduled refresh occurs.
- Batch file transmission introduces up to a 24-hour latency for real-time sales visibility.

Revisit when: business requirements demand real-time intra-day sales analytics across all branches.