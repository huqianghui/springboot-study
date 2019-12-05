DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles` (
  `articleId` int(11) NOT NULL,
  `title` varchar(500) DEFAULT NULL,
  `category` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`articleId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
--  Records of `Article`
-- ----------------------------
BEGIN;
INSERT INTO `articles` VALUES ('1', 'testTitle', 'testCategory');
COMMIT;