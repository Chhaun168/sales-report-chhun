# ADR-0001: Adopt modular monolith architecture for release 1

Status: Accepted
Date: 2026-10-19
Deciders: Chum Kimchhun

## Context
Forces that make this decision necessary: maintaining clean separation of concerns without deployment complexity (QA-4), ensuring fast local batch processing performance for monthly reports, and keeping development simple for a small student team under tight deadlines.

## Decision
We will structure the application as a single deployable artifact partitioned into strict package boundaries (`model`, `ingest`, `render`) with enforced unidirectional dependency rules.

## Alternatives considered
- Microservices architecture: highly scalable and independent, but introduces extreme deployment overhead, network latency, and operational complexity unsuitable for a single-team batch reporting system.
- Unstructured monolith: fast to hack together initially, but leads to tight coupling, spaghetti code, and maintenance bottlenecks violating modifiability requirements.

## Consequences
+ Clean package boundaries ensure high maintainability and straightforward unit testing (QA-4).
+ Simple single-process execution avoids distributed debugging hurdles.
- Shared memory space means a critical failure in one package can crash the entire JVM process.
- Scaling individual components independently is not possible without splitting the monolith later.

Revisit when: a month exceeds 10 million transactions or separate teams require independent deployment cycles.