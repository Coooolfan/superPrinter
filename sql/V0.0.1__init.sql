CREATE TABLE user
(
    user_id     BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(128) NOT NULL COMMENT '密码（加密存储）',
    role        INT                   DEFAULT 0 COMMENT '用户角色：0-普通用户，1-商户，2-管理员',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户信息表';

CREATE TABLE file_info
(
    file_id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    user_id       BIGINT       NOT NULL COMMENT '上传用户ID',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_name   VARCHAR(255) NOT NULL COMMENT 'MinIO中的对象名',
    file_type     VARCHAR(255) NOT NULL COMMENT '文件类型',
    file_size     BIGINT       NOT NULL COMMENT '文件大小（字节）',
    page_count    INT                   DEFAULT NULL COMMENT '页数（异步计算）',
    upload_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    FOREIGN KEY (user_id) REFERENCES user (user_id),
    INDEX idx_user_id (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='文件信息表';

CREATE TABLE printer_resource
(
    printer_id     BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '打印机ID',
    printer_name   VARCHAR(100) NOT NULL COMMENT '打印机名称',
    status         VARCHAR(20)  NOT NULL COMMENT '打印机状态：ONLINE,OFFLINE,OUT_OF_PAPER',
    paper_count    INT          NOT NULL DEFAULT 0 COMMENT 'A纸张数量',
    support_color  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '支持彩色打印 (0: 不支持, 1: 支持)',
    support_duplex TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '支持双面打印 (0: 不支持, 1: 支持)',
    paper_type     VARCHAR(255) NOT NULL DEFAULT '' COMMENT '支持的纸张类型 (例如：A4,A5,Letter,Legal 以逗号分隔)',
    version        INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    update_day     BIGINT       NOT NULL DEFAULT 0 COMMENT '更新时间,用于刷新特惠打印机余额',
    update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status) -- 保留状态索引
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='打印机资源表';

INSERT INTO printer_resource (printer_id, printer_name, status, paper_count, support_color, support_duplex, paper_type,
                              version, update_day, update_time)
VALUES (0, '特惠打印机', 'ONLINE', 10, 0, 0, 'A4', 0, 0, NOW());

CREATE TABLE print_order
(
    order_id         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    user_id          BIGINT         NOT NULL COMMENT '用户ID',
    file_ids         VARCHAR(255)   NOT NULL COMMENT '逗号分隔的文件ID列表',
    printer_id       BIGINT         NOT NULL COMMENT '打印机ID',
    copies           INT            NOT NULL DEFAULT 1 COMMENT '打印份数',
    paper_size       VARCHAR(50)    NOT NULL COMMENT '纸张类型',
    colorful         TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '是否彩色打印：0-黑白，1-彩色',
    double_sided     TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '是否双面打印：0-单面，1-双面',
    status           VARCHAR(20)    NOT NULL COMMENT '订单状态：CREATED,PAID,PROCESSING,READY_FOR_PICKUP,COMPLETED,CANCELLED',
    page_count       INT            NOT NULL DEFAULT 0 COMMENT '单份纸张数量',
    total_page_count INT            NOT NULL DEFAULT 0 COMMENT '订单总页数',
    amount           DECIMAL(10, 2) NOT NULL COMMENT '订单金额',
    pickup_code      VARCHAR(20)             DEFAULT NULL COMMENT '取件码',
    version          INT            NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_day       BIGINT         NOT NULL DEFAULT 0 COMMENT '创建日yyyyMMdd（加快索引判断，冗余字段）',
    create_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user (user_id),
    FOREIGN KEY (printer_id) REFERENCES printer_resource (printer_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_pickup_code (pickup_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='打印订单表';

CREATE TABLE order_status_log
(
    log_id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    order_id     BIGINT      NOT NULL COMMENT '订单ID',
    from_status  VARCHAR(20) NOT NULL COMMENT '原状态',
    to_status    VARCHAR(20) NOT NULL COMMENT '目标状态',
    operator     BIGINT      NOT NULL COMMENT '操作人ID',
    operate_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    remark       VARCHAR(255)         DEFAULT NULL COMMENT '备注',
    FOREIGN KEY (order_id) REFERENCES print_order (order_id),
    INDEX idx_order_id (order_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单状态流转日志表';

CREATE TABLE paper_type
(
    type_id        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '类型ID',
    type_name      VARCHAR(50)   NOT NULL COMMENT '纸张类型名称',
    price_per_page DECIMAL(5, 2) NOT NULL COMMENT '每页单价',
    is_available   TINYINT(1)    NOT NULL DEFAULT 1 COMMENT '是否可用：0-不可用，1-可用',
    create_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='纸张类型表';