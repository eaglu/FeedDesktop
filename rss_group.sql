/*
 Navicat Premium Data Transfer

 Source Server         : sqlite
 Source Server Type    : SQLite
 Source Server Version : 3030001
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3030001
 File Encoding         : 65001

 Date: 03/05/2023 15:00:28
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for rss_group
-- ----------------------------
DROP TABLE IF EXISTS "rss_group";
CREATE TABLE "rss_group" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "name" TEXT,
  UNIQUE ("name" ASC)
);

-- ----------------------------
-- Auto increment value for rss_group
-- ----------------------------
UPDATE "sqlite_sequence" SET seq = 47 WHERE name = 'rss_group';

-- ----------------------------
-- Indexes structure for table rss_group
-- ----------------------------
CREATE UNIQUE INDEX "rss_group_name"
ON "rss_group" (
  "name" ASC
);

PRAGMA foreign_keys = true;
