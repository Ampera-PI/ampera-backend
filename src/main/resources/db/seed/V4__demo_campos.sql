UPDATE usuario
SET telefone = '(13) 99999-0000', data_nascimento = DATE '2000-01-01'
WHERE email = 'demo@ampera.local';

UPDATE residencia
SET cidade = 'Santos', area_m2 = 80, numero_moradores = 3
WHERE nome = 'Casa';

UPDATE comodo
SET tipo = 'Sala', andar = 0, area_m2 = 20
WHERE nome = 'Sala';

UPDATE dispositivo
SET fabricante = 'Ampéra', potencia_nominal_w = 220, instalacao = DATE '2026-01-15'
WHERE codigo_mqtt = 'medidor-sala';

UPDATE sensor
SET modelo = CASE tipo WHEN 'TENSAO' THEN 'ZMPT101B' ELSE 'SCT-013' END,
    precisao = 1.5,
    calibrado_em = DATE '2026-01-15';

UPDATE tarifa
SET distribuidora = 'Distribuidora local', bandeira = 'VERDE'
WHERE distribuidora = '';

UPDATE meta_consumo
SET titulo = 'Meta de outubro'
WHERE titulo = 'Meta de consumo';
