/*
 Navicat Premium Data Transfer

 Source Server         : sqlite
 Source Server Type    : SQLite
 Source Server Version : 3030001
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3030001
 File Encoding         : 65001

 Date: 03/05/2023 15:10:53
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for rss_feed
-- ----------------------------
DROP TABLE IF EXISTS "rss_feed";
CREATE TABLE "rss_feed" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "title" TEXT NOT NULL,
  "link" TEXT NOT NULL,
  "group_id" INTEGER NOT NULL,
  FOREIGN KEY ("group_id") REFERENCES "rss_group" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  UNIQUE ("link" ASC)
);

-- ----------------------------
-- Auto increment value for rss_feed
-- ----------------------------
UPDATE "sqlite_sequence" SET seq = 57 WHERE name = 'rss_feed';

-- ----------------------------
-- Indexes structure for table rss_feed
-- ----------------------------
CREATE INDEX "rss_feed_group_id"
ON "rss_feed" (
  "group_id" ASC
);
CREATE UNIQUE INDEX "rss_feed_link"
ON "rss_feed" (
  "link" ASC
);

PRAGMA foreign_keys = true;
