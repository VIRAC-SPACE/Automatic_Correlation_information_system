INSERT INTO `roles` (`role_id`, `role_type`) VALUES (1, 'user');
INSERT INTO `roles` (`role_id`, `role_type`) VALUES (2, 'admin');

INSERT INTO `user` (`user_id`, `name`, `last_name`, `email`, `password`, `is_new`) VALUES (1, 'user', 'user', 'user@user.com', '$2a$10$DHkM0hMbYL9Oe1aAivriAuNLGqzqyFTvthJPlSRovGbLaPVE3J4hK', false);
INSERT INTO `user` (`user_id`, `name`, `last_name`, `email`, `password`, `is_new`) VALUES (2, 'Janis', 'Steinbergs', 'janis.steinbergs@venta.lv','$2a$10$DHkM0hMbYL9Oe1aAivriAuNLGqzqyFTvthJPlSRovGbLaPVE3J4hK', false);
INSERT INTO `user` (`user_id`, `name`, `last_name`, `email`, `password`, `is_new`) VALUES (3, 'admin', 'admin', 'admin@admin.lv','$2a$10$DHkM0hMbYL9Oe1aAivriAuNLGqzqyFTvthJPlSRovGbLaPVE3J4hK', false);
INSERT INTO `user` (`user_id`, `name`, `last_name`, `email`, `password`, `is_new`) VALUES (4, 'Karina', 'Skirmante', 'karina.krinkele@venta.lv','$2a$10$DHkM0hMbYL9Oe1aAivriAuNLGqzqyFTvthJPlSRovGbLaPVE3J4hK', false);
INSERT INTO `user` (`user_id`, `name`, `last_name`, `email`, `password`, `is_new`) VALUES (5, 'Ross', 'Burns', 'ross.burns@riken.jp','$2a$10$DHkM0hMbYL9Oe1aAivriAuNLGqzqyFTvthJPlSRovGbLaPVE3J4hK', false);
   
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES (1, 1);
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES (2, 2);
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES (3, 2);
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES (4, 1);
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES (5, 1);

INSERT INTO `user_info` (`user_info_id`,  `user_id`, `address1`, `phone`) VALUES (1, 3, 'Ventspils University of Applied Sciences, Inzenieru 101a, Ventspils, Latvia', ' (+371) 63629657');
INSERT INTO `user_info` (`user_info_id`,  `user_id`, `address1`, `phone`) VALUES (2, 4, 'Ventspils University of Applied Sciences, Inzenieru 101a, Ventspils, Latvia', ' (+371) 63629657');

INSERT INTO `station` (`station_id`, `short_title`, `long_title`, `pop_title`) VALUES (1, 'ir', 'IRBENE', 'RT-32');
INSERT INTO `station` (`station_id`, `short_title`, `long_title`, `pop_title`) VALUES (2, 'ib', 'IRBENE16', 'RT-16');
    		
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (1, 'AA', '06:00', 1.01667, 3);
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (2, 'AB', '06:00', 1.01667, 3);
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (3, 'AC', '06:00', 1.01667, 2);
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (4, 'B', '16:45', 1.93333, 3);
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (5, 'C', '16:45', 1.93333, 2);
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (6, 'D', '16:45', 1.93333, 3);
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (7, 'E', '18:55', 1.93333, 1);
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (8, 'F', '18:55', 1.93333, 1);
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (9, 'G', '18:55', 1.93333, 1);
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (10, 'H', '21:05', 1.91667, 2);
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (11, 'I', '21:05', 1.91667, 2);
INSERT INTO `group_obs` (`group_obs_id`, `group_obs_title`, `lstime`, `duration`, `counter`) VALUES (12, 'J', '23:15', 2.09167, 1);

INSERT INTO `global_parameters` (`global_parameter_id`, `parameter`, `param_value`) VALUES (1, 'stafile', '/sched/catalogs/sta.sess319.115m_RDBE.dat');
INSERT INTO `global_parameters` (`global_parameter_id`, `parameter`, `param_value`) VALUES (2, 'locfile', '/sched/catalogs/loc.sess319.115.dat');
INSERT INTO `global_parameters` (`global_parameter_id`, `parameter`, `param_value`) VALUES (3, 'sumitem', 'early,dwell,el1, slew');
INSERT INTO `global_parameters` (`global_parameter_id`, `parameter`, `param_value`) VALUES (4, 'optmode', 'scans');
INSERT INTO `global_parameters` (`global_parameter_id`, `parameter`, `param_value`) VALUES (5, 'autotape', '1');
INSERT INTO `global_parameters` (`global_parameter_id`, `parameter`, `param_value`) VALUES (6, 'opminant', '2');

INSERT INTO `observation_type` (`observation_type_id`, `type_title`) VALUES (1, 'VLBI');
INSERT INTO `observation_type` (`observation_type_id`, `type_title`) VALUES (2, 'CONFIG');
INSERT INTO `observation_type` (`observation_type_id`, `type_title`) VALUES (3, 'NONE');

INSERT INTO `correlation_type` (`correlation_type_id`, `type_title`) VALUES (1, 'JIVE');
INSERT INTO `correlation_type` (`correlation_type_id`, `type_title`) VALUES (2, 'Other');

INSERT INTO `pipeline` (`pipeline_id`, `title`) VALUES (1, 'VLBI pipeline');
INSERT INTO `pipeline` (`pipeline_id`, `title`) VALUES (2, 'VLBI 2 pipeline');
INSERT INTO `pipeline` (`pipeline_id`, `title`) VALUES (3, 'VIRAC AIPS single baseline pipeline 1');

INSERT INTO `project` (`project_id`, `title`) VALUES (1, 'IVARS');
INSERT INTO `project` (`project_id`, `title`) VALUES (2, 'STEF');
INSERT INTO `project` (`project_id`, `title`) VALUES (3, 'not project');

INSERT INTO `observationviracmode` (`observationviracmode_id`, `mode_title`, `number_of_channels`, `channels`) VALUES (1, 'all', 4096, '"CH01\", \"CH02\", \"CH03\", \"CH04\", \"CH05\", \"CH06\", \"CH07\", \"CH08\", \"CH09\", \"CH10\", \"CH11\", \"CH12\", \"CH13\", \"CH14\", \"CH15\", \"CH16\"' );
INSERT INTO `observationviracmode` (`observationviracmode_id`, `mode_title`, `number_of_channels`, `channels`) VALUES (2, 'line', 4096, '\"CH05\", \"CH06\"' );
INSERT INTO `observationviracmode` (`observationviracmode_id`, `mode_title`, `number_of_channels`, `channels`) VALUES (3, 'continum', 128,  '"CH01\", \"CH02\", \"CH03\", \"CH04\", \"CH05\", \"CH06\", \"CH07\", \"CH08\", \"CH09\", \"CH10\", \"CH11\", \"CH12\", \"CH13\", \"CH14\", \"CH15\", \"CH16\"' );







