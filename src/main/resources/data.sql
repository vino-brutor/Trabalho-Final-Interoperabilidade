-- Dados ficticios. CPFs e CNS sao inventados.
-- Os IDs sao gerados automaticamente na ordem de insercao (1, 2, 3...).

INSERT INTO paciente (nome, sobrenome, cpf, cns, data_nascimento, sexo, telefone, email, ativo) VALUES
('Maria Clara',   'Souza Pereira',  '52601815906', '702409624808093', '1985-03-12', 'FEMININO',  '(51) 99999-1001', 'maria.clara@exemplo.com', TRUE),
('João Pedro',    'Oliveira Santos','08301661305', NULL,              '1972-11-30', 'MASCULINO', '(51) 98888-2002', NULL,                      TRUE),
('Ana Luiza',     'Ferreira Costa', '18609139034', '708003205871234', '2001-07-05', 'FEMININO',  '(51) 97777-3003', 'ana.luiza@exemplo.com',   TRUE),
('Carlos Eduardo','Lima Rocha',     '99603082430', NULL,              '1958-01-22', 'MASCULINO', NULL,              NULL,                      FALSE),
('Alex',          'Moreira Silva',  '62819482112', '706403129887451', '1995-09-18', 'OUTRO',     '(51) 96666-5005', 'alex.moreira@exemplo.com',TRUE);

INSERT INTO sinal_vital (paciente_id, tipo, valor, unidade, data_hora_medicao, situacao) VALUES
(1, 'FREQUENCIA_CARDIACA',     78,   'bpm',  '2026-10-01 09:15:00', 'FINAL'),
(1, 'TEMPERATURA',             37.8, '°C',   '2026-10-01 09:15:00', 'FINAL'),
(1, 'PESO',                    64.5, 'kg',   '2026-10-01 09:16:00', 'FINAL'),
(2, 'SATURACAO_O2',            95,   '%',    '2026-09-20 13:50:00', 'FINAL'),
(2, 'FREQUENCIA_RESPIRATORIA', 18,   'irpm', '2026-09-20 13:50:00', 'PRELIMINAR'),
(3, 'ALTURA',                  165,  'cm',   '2026-08-20 10:05:00', 'FINAL'),
(3, 'TEMPERATURA',             36.5, '°C',   '2026-08-20 10:05:00', 'REGISTRADO_POR_ENGANO');

INSERT INTO alergia (paciente_id, substancia, tipo, categoria, criticidade, situacao_clinica, verificacao, reacao, data_registro) VALUES
(1, 'Dipirona',   'ALERGIA',      'MEDICAMENTO', 'ALTA',          'ATIVA',     'CONFIRMADA',            'Urticária',           '2024-05-10'),
(1, 'Lactose',    'INTOLERANCIA', 'ALIMENTO',    'BAIXA',         'ATIVA',     'NAO_CONFIRMADA',        'Distensão abdominal', '2025-02-01'),
(2, 'Pólen',      'ALERGIA',      'AMBIENTE',    'BAIXA',         'RESOLVIDA', 'CONFIRMADA',            'Rinite',              '2019-09-15'),
(3, 'Penicilina', 'ALERGIA',      'MEDICAMENTO', 'INDETERMINADA', NULL,        'REGISTRADA_POR_ENGANO', NULL,                  '2026-08-20');

INSERT INTO vacinacao (paciente_id, codigo_vacina, nome_vacina, data_aplicacao, lote, dose, situacao) VALUES
(1, '37',  'Febre amarela',                                   '2023-04-10', 'FA2304B', 1,    'APLICADA'),
(1, '03',  'Tríplice viral (sarampo, caxumba e rubéola)',     '2020-06-01', 'TV0601A', 2,    'APLICADA'),
(2, '88',  'Influenza',                                       '2025-05-02', 'FL0502K', NULL, 'APLICADA'),
(3, '115', 'dTpa (tríplice bacteriana acelular do tipo adulto)', '2026-02-10', NULL,   NULL, 'NAO_APLICADA'),
(5, '21',  'Varicela',                                        '2019-03-08', 'VR0308Z', 1,    'APLICADA');

INSERT INTO atendimento (paciente_id, tipo, situacao, inicio, fim, motivo) VALUES
(1, 'AMBULATORIAL', 'FINALIZADO',   '2026-10-01 09:00:00', '2026-10-01 09:40:00', 'Febre e dor de garganta há 3 dias'),
(2, 'TELECONSULTA', 'FINALIZADO',   '2026-09-20 14:00:00', '2026-09-20 14:25:00', 'Renovação de receita'),
(3, 'AMBULATORIAL', 'AGENDADO',     '2026-11-05 10:00:00', NULL,                  'Consulta de rotina'),
(5, 'DOMICILIAR',   'EM_ANDAMENTO', '2026-10-24 08:30:00', NULL,                  'Curativo pós-operatório'),
(4, 'EMERGENCIA',   'CANCELADO',    '2026-07-12 22:10:00', NULL,                  'Dor torácica');
