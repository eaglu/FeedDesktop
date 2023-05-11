/*
 Navicat Premium Data Transfer

 Source Server         : sqlite
 Source Server Type    : SQLite
 Source Server Version : 3030001
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3030001
 File Encoding         : 65001

 Date: 03/05/2023 15:57:01
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for setting
-- ----------------------------
DROP TABLE IF EXISTS "setting";
CREATE TABLE "setting" (
  "sync_in_launch" INTEGER NOT NULL DEFAULT 0,
  "save_time" INTEGER NOT NULL DEFAULT 14,
  "page_in_launch" INTEGER NOT NULL DEFAULT 0,
  "id" INTEGER PRIMARY KEY AUTOINCREMENT
);

-- ----------------------------
-- Auto increment value for setting
-- ----------------------------
UPDATE "sqlite_sequence" SET seq = 1 WHERE name = 'setting';

PRAGMA foreign_keys = true;
