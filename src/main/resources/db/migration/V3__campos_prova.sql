ALTER TABLE usuario
    ADD COLUMN telefone         VARCHAR(20) NOT NULL DEFAULT '',
    ADD COLUMN data_nascimento  DATE,
    ADD COLUMN ativo            BOOLEAN     NOT NULL DEFAULT TRUE;

ALTER TABLE residencia
    ADD COLUMN cidade            VARCHAR(80)      NOT NULL DEFAULT '',
    ADD COLUMN area_m2           DOUBLE PRECISION NOT NULL DEFAULT 0,
    ADD COLUMN numero_moradores  INTEGER          NOT NULL DEFAULT 1;

ALTER TABLE comodo
    ADD COLUMN tipo     VARCHAR(40)      NOT NULL DEFAULT 'Outro',
    ADD COLUMN andar    INTEGER          NOT NULL DEFAULT 0,
    ADD COLUMN area_m2  DOUBLE PRECISION NOT NULL DEFAULT 0,
    ADD COLUMN ativo    BOOLEAN          NOT NULL DEFAULT TRUE;

ALTER TABLE dispositivo
    ADD COLUMN fabricante          VARCHAR(80)      NOT NULL DEFAULT '',
    ADD COLUMN potencia_nominal_w  DOUBLE PRECISION NOT NULL DEFAULT 0,
    ADD COLUMN instalacao          DATE,
    ADD COLUMN ativo               BOOLEAN          NOT NULL DEFAULT TRUE;

ALTER TABLE sensor
    ADD COLUMN modelo        VARCHAR(80)      NOT NULL DEFAULT '',
    ADD COLUMN precisao      DOUBLE PRECISION NOT NULL DEFAULT 0,
    ADD COLUMN calibrado_em  DATE,
    ADD COLUMN ativo         BOOLEAN          NOT NULL DEFAULT TRUE;

ALTER TABLE medicao
    ADD COLUMN observacao  VARCHAR(200) NOT NULL DEFAULT '',
    ADD COLUMN validada    BOOLEAN      NOT NULL DEFAULT TRUE;

ALTER TABLE tarifa
    ADD COLUMN distribuidora  VARCHAR(80) NOT NULL DEFAULT '',
    ADD COLUMN bandeira       VARCHAR(20) NOT NULL DEFAULT 'VERDE',
    ADD COLUMN ativa          BOOLEAN     NOT NULL DEFAULT TRUE;

ALTER TABLE meta_consumo
    ADD COLUMN titulo  VARCHAR(120) NOT NULL DEFAULT 'Meta de consumo',
    ADD COLUMN ativa   BOOLEAN      NOT NULL DEFAULT TRUE;

ALTER TABLE relatorio
    ADD COLUMN titulo  VARCHAR(120) NOT NULL DEFAULT 'Relatório';
