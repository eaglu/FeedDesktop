/*
 Navicat Premium Data Transfer

 Source Server         : sqlite
 Source Server Type    : SQLite
 Source Server Version : 3030001
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3030001
 File Encoding         : 65001

 Date: 04/05/2023 22:25:09
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for rss_item2
-- ----------------------------
DROP TABLE IF EXISTS "rss_item2";
CREATE TABLE "rss_item2" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "title" TEXT NOT NULL,
  "link" TEXT NOT NULL,
  "description" TEXT NOT NULL,
  "author" TEXT,
  "feed_id" TEXT NOT NULL,
  "is_unread" INTEGER DEFAULT 1,
  "is_stared" INTEGER DEFAULT 0,
  "pub_date" DATE,
  CONSTRAINT "item_feeds_id" FOREIGN KEY ("feed_id") REFERENCES "rss_feed" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  UNIQUE ("link" ASC)
);

-- ----------------------------
-- Auto increment value for rss_item2
-- ----------------------------
UPDATE "sqlite_sequence" SET seq = 4011 WHERE name = 'rss_item2';

-- ----------------------------
-- Indexes structure for table rss_item2
-- ----------------------------
CREATE INDEX "rss_item_pub_date"
ON "rss_item2" (
  "pub_date" ASC
);

PRAGMA foreign_keys = true;
