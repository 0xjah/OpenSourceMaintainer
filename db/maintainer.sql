-- Open Source Maintainer Database
-- Database: maintainer

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `maintainer`
--
CREATE DATABASE IF NOT EXISTS `maintainer` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `maintainer`;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `UserId` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(64) NOT NULL,
  `Role` enum('ADMIN','MAINTAINER') NOT NULL DEFAULT 'MAINTAINER',
  `Password` varchar(64) NOT NULL,
  `Username` varchar(32) NOT NULL,
  `Email` varchar(128) DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`UserId`),
  UNIQUE KEY `Username` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`UserId`, `Name`, `Role`, `Password`, `Username`, `Email`) VALUES
(1, 'Admin User', 'ADMIN', 'admin', 'admin', 'admin@localhost'),
(2, 'Maintainer User', 'MAINTAINER', 'user', 'user', 'user@localhost');

-- --------------------------------------------------------

--
-- Table structure for table `project`
--

CREATE TABLE `project` (
  `ProjectId` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  `Description` text DEFAULT NULL,
  `RepoUrl` varchar(256) DEFAULT NULL,
  `Language` varchar(32) DEFAULT NULL,
  `Status` enum('ACTIVE','ARCHIVED','MAINTENANCE') NOT NULL DEFAULT 'ACTIVE',
  `Stars` int(11) DEFAULT 0,
  `Forks` int(11) DEFAULT 0,
  `OwnerId` int(11) NOT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ProjectId`),
  KEY `OwnerId` (`OwnerId`),
  CONSTRAINT `project_ibfk_1` FOREIGN KEY (`OwnerId`) REFERENCES `user` (`UserId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `project`
--

INSERT INTO `project` (`ProjectId`, `Name`, `Description`, `RepoUrl`, `Language`, `Status`, `Stars`, `Forks`, `OwnerId`) VALUES
(1, 'core-lib', 'Core utility library', 'https://github.com/example/core-lib', 'Java', 'ACTIVE', 142, 23, 1),
(2, 'api-gateway', 'REST API Gateway service', 'https://github.com/example/api-gateway', 'Go', 'ACTIVE', 89, 12, 1),
(3, 'legacy-module', 'Legacy support module', 'https://github.com/example/legacy-module', 'Python', 'MAINTENANCE', 34, 5, 2);

-- --------------------------------------------------------

--
-- Table structure for table `issue`
--

CREATE TABLE `issue` (
  `IssueId` int(11) NOT NULL AUTO_INCREMENT,
  `ProjectId` int(11) NOT NULL,
  `Title` varchar(256) NOT NULL,
  `Description` text DEFAULT NULL,
  `Status` enum('OPEN','IN_PROGRESS','RESOLVED','CLOSED') NOT NULL DEFAULT 'OPEN',
  `Priority` enum('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL DEFAULT 'MEDIUM',
  `Type` enum('BUG','FEATURE','DOCS','SECURITY') NOT NULL DEFAULT 'BUG',
  `AssigneeId` int(11) DEFAULT NULL,
  `ReporterId` int(11) DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `UpdatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ClosedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`IssueId`),
  KEY `ProjectId` (`ProjectId`),
  KEY `AssigneeId` (`AssigneeId`),
  KEY `ReporterId` (`ReporterId`),
  CONSTRAINT `issue_ibfk_1` FOREIGN KEY (`ProjectId`) REFERENCES `project` (`ProjectId`) ON DELETE CASCADE,
  CONSTRAINT `issue_ibfk_2` FOREIGN KEY (`AssigneeId`) REFERENCES `user` (`UserId`) ON DELETE SET NULL,
  CONSTRAINT `issue_ibfk_3` FOREIGN KEY (`ReporterId`) REFERENCES `user` (`UserId`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `issue`
--

INSERT INTO `issue` (`IssueId`, `ProjectId`, `Title`, `Description`, `Status`, `Priority`, `Type`, `AssigneeId`, `ReporterId`) VALUES
(1, 1, 'Memory leak in cache module', 'Memory usage increases over time when using cache', 'OPEN', 'HIGH', 'BUG', 1, 2),
(2, 1, 'Add retry logic', 'Implement exponential backoff for failed requests', 'IN_PROGRESS', 'MEDIUM', 'FEATURE', 1, 1),
(3, 2, 'Rate limiting not working', 'Rate limiter bypassed under heavy load', 'OPEN', 'CRITICAL', 'SECURITY', NULL, 2),
(4, 2, 'Update API docs', 'Document new endpoints added in v2.0', 'RESOLVED', 'LOW', 'DOCS', 2, 1),
(5, 3, 'Deprecation warnings', 'Fix deprecated function calls', 'OPEN', 'MEDIUM', 'BUG', 2, 2);

-- --------------------------------------------------------

--
-- Table structure for table `contributor`
--

CREATE TABLE `contributor` (
  `ContributorId` int(11) NOT NULL AUTO_INCREMENT,
  `ProjectId` int(11) NOT NULL,
  `Name` varchar(64) NOT NULL,
  `GithubHandle` varchar(64) DEFAULT NULL,
  `Commits` int(11) DEFAULT 0,
  `Additions` int(11) DEFAULT 0,
  `Deletions` int(11) DEFAULT 0,
  `LastContribution` datetime DEFAULT NULL,
  PRIMARY KEY (`ContributorId`),
  KEY `ProjectId` (`ProjectId`),
  CONSTRAINT `contributor_ibfk_1` FOREIGN KEY (`ProjectId`) REFERENCES `project` (`ProjectId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `contributor`
--

INSERT INTO `contributor` (`ContributorId`, `ProjectId`, `Name`, `GithubHandle`, `Commits`, `Additions`, `Deletions`, `LastContribution`) VALUES
(1, 1, 'Alice Dev', 'alicedev', 45, 2340, 890, '2026-01-03 14:30:00'),
(2, 1, 'Bob Coder', 'bobcoder', 23, 1200, 450, '2026-01-02 09:15:00'),
(3, 2, 'Charlie Ops', 'charlieops', 67, 4500, 1200, '2026-01-04 11:00:00'),
(4, 3, 'Dana Maintainer', 'danamaint', 12, 340, 120, '2025-12-28 16:45:00');

-- --------------------------------------------------------

--
-- Table structure for table `activity_log`
--

CREATE TABLE `activity_log` (
  `LogId` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) DEFAULT NULL,
  `ProjectId` int(11) DEFAULT NULL,
  `Action` varchar(64) NOT NULL,
  `Details` text DEFAULT NULL,
  `CreatedAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`LogId`),
  KEY `UserId` (`UserId`),
  KEY `ProjectId` (`ProjectId`),
  CONSTRAINT `activity_log_ibfk_1` FOREIGN KEY (`UserId`) REFERENCES `user` (`UserId`) ON DELETE SET NULL,
  CONSTRAINT `activity_log_ibfk_2` FOREIGN KEY (`ProjectId`) REFERENCES `project` (`ProjectId`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `activity_log`
--

INSERT INTO `activity_log` (`LogId`, `UserId`, `ProjectId`, `Action`, `Details`) VALUES
(1, 1, 1, 'ISSUE_CREATED', 'Created issue: Memory leak in cache module'),
(2, 1, 2, 'PROJECT_UPDATED', 'Updated project status'),
(3, 2, 1, 'ISSUE_ASSIGNED', 'Assigned issue to maintainer');

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
