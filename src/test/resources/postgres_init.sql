CREATE TABLE product_defect(
    product_id  integer not null ,
    inspect_date timestamp not null ,
    defect_id   integer not null ,
    auto_code   char(2),
    manual_code char(2),
    primary key (product_id, inspect_date, defect_id)
);

CREATE TABLE product_defect_attr (
    uid         serial primary key,
    product_id  integer not null ,
    inspect_date timestamp not null ,
    defect_id   integer not null ,
    type        varchar(20) not null ,
    val         varchar(20)
);

CREATE INDEX IF NOT EXISTS product_defect_attr_product_id_inspect_date_defect_id_index
    on public.product_defect_attr (product_id, inspect_date, defect_id);

INSERT INTO product_defect (product_id, inspect_date, defect_id, auto_code)
VALUES
    (1, '2023-11-30 13:24:56', 1, '1C'),
    (1, '2023-11-30 13:24:56', 2, '2B'),
    (1, '2023-11-30 13:24:56', 3, '3C'),
    (1, '2023-11-30 13:24:56', 4, '4D'),
    (1, '2023-11-30 13:24:56', 5, '1C'),
    (2, '2023-12-03 13:24:56', 1, '2B'),
    (2, '2023-12-03 13:24:56', 2, '5B'),
    (2, '2023-12-03 13:24:56', 3, '4C'),
    (2, '2023-12-03 13:24:56', 4, '1C'),
    (2, '2023-12-03 13:24:56', 5, '1D');

INSERT INTO product_defect_attr (product_id, inspect_date, defect_id, type, val)
VALUES
    (1, '2023-11-30 13:24:56', 1, 'MANUAL_JUDGE_1ST', '2C'),
    (1, '2023-11-30 13:24:56', 1, 'MANUAL_JUDGE_2ND', '2D'),
    (1, '2023-11-30 13:24:56', 1, 'MANUAL_JUDGE_3RD', '3B'),
    (1, '2023-11-30 13:24:56', 1, 'Non judge', 'babla');
