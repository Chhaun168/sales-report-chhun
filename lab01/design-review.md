# Lab 01 Design Review

## Package Responsibilities and Coupling (Ca, Ce, Instability I = Ce / (Ca + Ce))
- **`edu.itc.salesreport.model`**: Defines the core domain entities, value objects, and business invariants (High cohesion). 
  - Afferent Coupling (Ca): 3 (used by ingest, render, and tests)
  - Efferent Coupling (Ce): 0 (independent of other project packages)
  - Instability ($I$): $0 / (3 + 0) = 0.0$ (maximally stable foundation)
- **`edu.itc.salesreport.ingest`**: Handles file loading, parsing raw CSV entries into records, and notifying observers of progress.
  - Afferent Coupling (Ca): 1 (used by root/app and tests)
  - Efferent Coupling (Ce): 1 (depends on model)
  - Instability ($I$): $1 / (1 + 1) = 0.5$
- **`edu.itc.salesreport.render`**: Manages output formatting and reporting strategies via renderers and console notifications.
  - Afferent Coupling (Ca): 0
  - Efferent Coupling (Ce): 2 (depends on model and ingest)
  - Instability ($I$): $2 / (0 + 2) = 1.0$ (maximally instantiable/changeable layer)

## Design Choices
- **Coupling Acceptance**: The `render` package depends on `model` to extract reporting statistics. This coupling is accepted because renderers inherently require access to domain objects to format output representations.
- **Sealed vs Open Interfaces**: A sealed interface (`ReportRenderer`) is chosen over an open interface to enforce compile-time exhaustiveness when selecting export formats (text, HTML, CSV) and prevents arbitrary external implementations, fulfilling modibility quality-attribute scenarios (QA-4).