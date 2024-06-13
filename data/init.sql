-- MySQL dump 10.13  Distrib 8.4.0, for Linux (x86_64)
--
-- Host: localhost    Database: votingapp
-- ------------------------------------------------------
-- Server version	8.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `options`
--

DROP TABLE IF EXISTS `options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `options` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `question_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5bmv46so2y5igt9o9n9w4fh6y` (`question_id`),
  CONSTRAINT `FK5bmv46so2y5igt9o9n9w4fh6y` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `private_voters`
--

DROP TABLE IF EXISTS `private_voters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `private_voters` (
  `voting_id` bigint NOT NULL,
  `voter_id` bigint NOT NULL,
  PRIMARY KEY (`voting_id`,`voter_id`),
  KEY `FK8ttdlyh21u3xddw5nrwr8ioxn` (`voter_id`),
  CONSTRAINT `FK8ajpu1p4pk016yms8udif1cmo` FOREIGN KEY (`voting_id`) REFERENCES `votings` (`id`),
  CONSTRAINT `FK8ttdlyh21u3xddw5nrwr8ioxn` FOREIGN KEY (`voter_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `voting_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKps28vyj9c7jowmo7t7n7h44rs` (`voting_id`),
  CONSTRAINT `FKps28vyj9c7jowmo7t7n7h44rs` FOREIGN KEY (`voting_id`) REFERENCES `votings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `votes`
--

DROP TABLE IF EXISTS `votes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `votes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `voter_id` bigint DEFAULT NULL,
  `voting_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK94ah680c6uad0t12uj2ib9nam` (`voter_id`),
  KEY `FK7109e6nhyht4a0qqijtu3ovl1` (`voting_id`),
  CONSTRAINT `FK7109e6nhyht4a0qqijtu3ovl1` FOREIGN KEY (`voting_id`) REFERENCES `votings` (`id`),
  CONSTRAINT `FK94ah680c6uad0t12uj2ib9nam` FOREIGN KEY (`voter_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `votes_questions_options`
--

DROP TABLE IF EXISTS `votes_questions_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `votes_questions_options` (
  `option_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `vote_id` bigint NOT NULL,
  PRIMARY KEY (`option_id`,`question_id`,`vote_id`),
  KEY `FKp2017fitu1bk2kk4o18erws5r` (`question_id`),
  KEY `FK4cx39yhyn3fklrxewl44kbrv1` (`vote_id`),
  CONSTRAINT `FK4cx39yhyn3fklrxewl44kbrv1` FOREIGN KEY (`vote_id`) REFERENCES `votes` (`id`),
  CONSTRAINT `FKkt91w2uj06yqs2yvccs0l17py` FOREIGN KEY (`option_id`) REFERENCES `options` (`id`),
  CONSTRAINT `FKp2017fitu1bk2kk4o18erws5r` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `votings`
--

DROP TABLE IF EXISTS `votings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `votings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `creationdate` datetime(6) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `enddate` datetime(6) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `privatevoting` bit(1) NOT NULL,
  `secretvotes` bit(1) NOT NULL,
  `showstats` bit(1) NOT NULL,
  `showstatsrealtime` bit(1) NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `creator_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKherg9tc4g2utueh87a5cyut3t` (`creator_id`),
  CONSTRAINT `FKherg9tc4g2utueh87a5cyut3t` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-13 16:55:34
