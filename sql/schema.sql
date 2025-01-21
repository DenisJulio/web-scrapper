CREATE TABLE Mercado (
    id SERIAL PRIMARY KEY,
    sigla VARCHAR(4) NOT NULL,
    regiao VARCHAR(30) NOT NULL
);

CREATE TABLE BoletimDiario (
    id SERIAL PRIMARY KEY,
    mercado_id INTEGER NOT NULL REFERENCES Mercado(id) ON DELETE CASCADE,
    data DATE NOT NULL
);

CREATE TABLE InformacaoProduto (
    id SERIAL PRIMARY KEY,
    boletim_id INTEGER NOT NULL REFERENCES BoletimDiario(id) ON DELETE CASCADE,
    nome VARCHAR(50) NOT NULL,
    embalagem VARCHAR(20) NOT NULL,
    preco_minimo NUMERIC(10, 2) NOT NULL,
    preco_comum NUMERIC(10, 2) NOT NULL,
    preco_maximo NUMERIC(10, 2) NOT NULL,
    situacao VARCHAR(5) NOT NULL
);
