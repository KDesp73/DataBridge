# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [2.0.13] - 2024-11-11 

### Added

- Checking hashes and reruning migrations
- Implemented Scheman (Migration Utility)
- Added cli for Scheman

### Changed

- Updated version to 2.0.13


## [2.0.12] - 2024-11-11 

### Added

- Added TransactionManager class
- Added Adapter class
- Added Config class to handle database information
- Added Again class retry mechanism

### Changed

- Updated SQLogger
- Updated version to 2.0.12

### Removed

- Removed jdbc drivers from pom.xml

### Fixed

- More robust connections


## [2.0.11] - 2024-11-05 

### Changed

- DatabaseConnection extends AutoCloseable


## [2.0.10] - 2024-11-04 

### Added

- Added SQLogger

### Changed

- updated version to 2.0.10


## [2.0.9] - 2024-11-03 

### Added

- added callProcedure method to PostgresConnection
- added callFunction method to PostgresConnection

### Changed

- updated version to 2.0.9


## [2.0.8] - 2024-10-30 

### Added

- Added Postgresql driver and connection


[2.0.10]: https://github.com/KDesp73/DataBridge/releases/tag/v2.0.10
[2.0.11]: https://github.com/KDesp73/DataBridge/releases/tag/v2.0.11
[2.0.12]: https://github.com/KDesp73/DataBridge/releases/tag/v2.0.12
[2.0.13]: https://github.com/KDesp73/DataBridge/releases/tag/v2.0.13
[2.0.7]: https://github.com/KDesp73/DataBridge/releases/tag/v2.0.7
[2.0.8]: https://github.com/KDesp73/DataBridge/releases/tag/v2.0.8
[2.0.9]: https://github.com/KDesp73/DataBridge/releases/tag/v2.0.9

