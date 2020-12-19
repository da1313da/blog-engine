INSERT INTO `blog-engine`.users
(id, code, email, is_moderator, name, password, photo, reg_time)
VALUES(2, NULL, 'vasya@mail.ru', 0, 'Vasya', '$2a$12$r.bq5Qz8qGNdx.jMlUrEFOPsLNU6Yjw0JuVoWpxCDruLzKis8eu/S', NULL, '2020-12-19 22:06:02');
INSERT INTO `blog-engine`.posts
(id, is_active, moderation_status, `text`, `time`, title, view_count, moderator_id, user_id)
VALUES(1, 1, 'ACCEPTED', '<span style="font-weight: bold;">Первый пост будет по Java!</span>', '2020-12-19 22:04:44', 'Это будет первый пост!!', 1, NULL, 1);
INSERT INTO `blog-engine`.posts
(id, is_active, moderation_status, `text`, `time`, title, view_count, moderator_id, user_id)
VALUES(2, 0, 'NEW', '<span style="font-weight: bold;">Пост на новый год!!</span><div><span style="font-weight: bold;"><br></span></div>', '2020-12-30 22:06:00', 'Это будет пост отложенный на новый год!!', 0, NULL, 2);
INSERT INTO `blog-engine`.posts
(id, is_active, moderation_status, `text`, `time`, title, view_count, moderator_id, user_id)
VALUES(3, 1, 'ACCEPTED', 'Лучшее кофе на этой дороге отхлебнешь - протянеш ноги!', '2020-12-19 22:08:25', 'Лучшее кофе', 0, 1, 2);
INSERT INTO `blog-engine`.posts
(id, is_active, moderation_status, `text`, `time`, title, view_count, moderator_id, user_id)
VALUES(4, 1, 'DECLINED', 'Пост наверняка заангажирован ч чью-то пользу!&nbsp;', '2020-12-19 22:30:50', 'Политический пост!!', 0, 1, 2);
INSERT INTO `blog-engine`.posts
(id, is_active, moderation_status, `text`, `time`, title, view_count, moderator_id, user_id)
VALUES(5, 1, 'ACCEPTED', 'Hibertane это отстой!&nbsp;<div>Причем полный!&nbsp;</div><div>И короткий!</div>', '2020-12-19 22:40:00', 'Второй пост про Hibernate!', 1, NULL, 1);
INSERT INTO `blog-engine`.posts
(id, is_active, moderation_status, `text`, `time`, title, view_count, moderator_id, user_id)
VALUES(6, 1, 'ACCEPTED', '<b>Spring!</b><div>Spring! spring!</div><div>Spring! spring! spring!</div>', '2020-12-19 22:54:02', 'Третий пост про Spring!', 2, NULL, 1);
INSERT INTO `blog-engine`.posts
(id, is_active, moderation_status, `text`, `time`, title, view_count, moderator_id, user_id)
VALUES(7, 1, 'NEW', 'Даешь модерацию! Как можно скорее! Достали это 50 символов нужно сменить!', '2020-12-19 22:57:10', 'Когда-же меня замодерируют уже!!', 0, NULL, 2);
INSERT INTO `blog-engine`.posts
(id, is_active, moderation_status, `text`, `time`, title, view_count, moderator_id, user_id)
VALUES(8, 1, 'ACCEPTED', '<div>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod</div><div>tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,</div><div>quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo</div><div>consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse</div><div>cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non</div><div>proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</div>', '2020-12-19 23:04:36', 'Еще одна про кофе для счета!', 0, 1, 2);
INSERT INTO `blog-engine`.tags
(id, name)
VALUES(4, 'JAVA');
INSERT INTO `blog-engine`.tags
(id, name)
VALUES(5, 'НОВЫЙ ГОД');
INSERT INTO `blog-engine`.tags
(id, name)
VALUES(6, 'КОФЕ');
INSERT INTO `blog-engine`.tags
(id, name)
VALUES(7, 'ПОЛИТОТА');
INSERT INTO `blog-engine`.tags
(id, name)
VALUES(8, 'HIBERNATE');
INSERT INTO `blog-engine`.tags
(id, name)
VALUES(9, 'SPRING');
INSERT INTO `blog-engine`.tags
(id, name)
VALUES(10, 'КОФЕ');
INSERT INTO `blog-engine`.tag2post
(tag_id, post_id)
VALUES(4, 1);
INSERT INTO `blog-engine`.tag2post
(tag_id, post_id)
VALUES(5, 2);
INSERT INTO `blog-engine`.tag2post
(tag_id, post_id)
VALUES(6, 3);
INSERT INTO `blog-engine`.tag2post
(tag_id, post_id)
VALUES(7, 4);
INSERT INTO `blog-engine`.tag2post
(tag_id, post_id)
VALUES(8, 5);
INSERT INTO `blog-engine`.tag2post
(tag_id, post_id)
VALUES(9, 6);
INSERT INTO `blog-engine`.tag2post
(tag_id, post_id)
VALUES(6, 7);
INSERT INTO `blog-engine`.tag2post
(tag_id, post_id)
VALUES(6, 8);
INSERT INTO `blog-engine`.tag2post
(tag_id, post_id)
VALUES(10, 8);
INSERT INTO `blog-engine`.post_votes
(id, `time`, value, post_id, user_id)
VALUES(1, '2020-12-19 22:34:42', 1, 3, 1);
INSERT INTO `blog-engine`.post_votes
(id, `time`, value, post_id, user_id)
VALUES(2, '2020-12-19 22:59:13', -1, 5, 2);
INSERT INTO `blog-engine`.post_votes
(id, `time`, value, post_id, user_id)
VALUES(3, '2020-12-19 23:09:43', 1, 6, 2);
INSERT INTO `blog-engine`.post_votes
(id, `time`, value, post_id, user_id)
VALUES(4, '2020-12-19 23:09:50', 1, 1, 2);
INSERT INTO `blog-engine`.post_votes
(id, `time`, value, post_id, user_id)
VALUES(5, '2020-12-19 23:10:23', -1, 8, 1);
INSERT INTO `blog-engine`.post_comments
(id, `text`, `time`, parent_id, post_id, user_id)
VALUES(1, 'Это будет первый комментарий!', '2020-12-19 22:06:36', NULL, 1, 2);
INSERT INTO `blog-engine`.post_comments
(id, `text`, `time`, parent_id, post_id, user_id)
VALUES(2, 'Написал комент сам себе, так можно?', '2020-12-19 22:31:23', NULL, 3, 2);
INSERT INTO `blog-engine`.post_comments
(id, `text`, `time`, parent_id, post_id, user_id)
VALUES(3, 'Нельзя!! Надо поправить!', '2020-12-19 22:32:19', NULL, 3, 1);
INSERT INTO `blog-engine`.post_comments
(id, `text`, `time`, parent_id, post_id, user_id)
VALUES(4, 'Просмотры увеличиваются постоянно!!', '2020-12-19 22:58:14', NULL, 6, 2);
INSERT INTO `blog-engine`.post_comments
(id, `text`, `time`, parent_id, post_id, user_id)
VALUES(5, 'Можно бомбить несколько коментариев?', '2020-12-19 22:58:38', NULL, 6, 2);
INSERT INTO `blog-engine`.post_comments
(id, `text`, `time`, parent_id, post_id, user_id)
VALUES(6, 'Еще один!', '2020-12-19 22:59:28', NULL, 5, 2);
INSERT INTO `blog-engine`.post_comments
(id, `text`, `time`, parent_id, post_id, user_id)
VALUES(7, '<strong>Vasya</strong>, и тут поправить!', '2020-12-19 23:11:34', 2, 3, 1);
UPDATE `blog-engine`.global_settings
SET code='MULTIUSER_MODE', name='Многопользовательский режим', value='YES'
WHERE id=1;
UPDATE `blog-engine`.global_settings
SET code='POST_PREMODERATION', name='Премодерация постов', value='YES'
WHERE id=2;
UPDATE `blog-engine`.global_settings
SET code='STATISTICS_IS_PUBLIC', name='Показывать всем статистику блога', value='YES'
WHERE id=3;