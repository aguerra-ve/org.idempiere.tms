# Inbox — drop any raw data here

This is the ingest folder of the knowledge base. Place PDFs, images, CSV/JSON exports, notes, HTML, office documents, or other source material here and ask the agent to **process the inbox**.

The sweep will:

1. Detect and extract each format.
2. Classify it by subject rather than file extension.
3. Preserve the original under `raw/<topic>/_originals/`.
4. Compile durable knowledge into cross-linked wiki articles.
5. Move successfully processed arrivals to `_processed/YYYY-MM-DD/`.

Unreadable files remain visible in the inbox and create a knowledge gap; they are never silently discarded.

`_processed/` is a local audit trail and is ignored by Git. The durable source copy remains under `raw/`.
