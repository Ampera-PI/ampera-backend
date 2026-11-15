"""Engenharia reversa limitada às classes do caso de uso de relatório.

Extrai nomes, membros declarados e referências lexicais dos arquivos Kotlin.
Não é um compilador Kotlin: as setas representam uso de um tipo no fonte,
sem inferir cardinalidade, despacho ou fluxo de execução.
Gera PlantUML e Mermaid sem instalar bibliotecas ou consultar serviços externos.
"""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
MAIN = ROOT / "src/main/kotlin/com/ampera"
TEST = ROOT / "src/test/kotlin/com/ampera"
FILES = [
    MAIN / "application/RelatorioService.kt",
    MAIN / "domain/ConsumoDoPeriodo.kt",
    MAIN / "domain/PrevisorDeConsumo.kt",
    MAIN / "domain/CalculadoraDeCusto.kt",
    MAIN / "infrastructure/persistence/Relatorio.kt",
    MAIN / "mapper/RelatorioMapper.kt",
    TEST / "application/RelatorioServiceTest.kt",
    TEST / "domain/ConsumoDoPeriodoTest.kt",
    TEST / "domain/PrevisorDeConsumoTest.kt",
    TEST / "domain/CalculadoraDeCustoTest.kt",
    TEST / "domain/RegrasTest.kt",
]


def main():
    classes = {}
    for path in FILES:
        fonte = path.read_text(encoding="utf-8")
        # Comentários não devem criar dependências artificiais.
        fonte = re.sub(r"/\*.*?\*/|//[^\n]*", "", fonte, flags=re.S)
        nome = re.search(r"\bclass\s+(\w+)", fonte).group(1)
        pacote = re.search(r"^package\s+([\w.]+)", fonte, re.M).group(1)
        membros = []
        if "/test/" not in path.as_posix():
            # Neste recorte, os atributos são declarados no construtor primário.
            # Restringir ao cabeçalho evita confundir variáveis locais com atributos.
            cabecalho = fonte.split("{", 1)[0]
            for campo, tipo in re.findall(r"\b(?:val|var)\s+(\w+)\s*:\s*([^=,\n]+)", cabecalho):
                membros.append(f"{campo}: {tipo.strip()}")
            for metodo, parametros, retorno in re.findall(r"\bfun\s+(\w+)\s*\(([^)]*)\)\s*(?::\s*([^=\n{]+))?", fonte):
                parametros = " ".join(parametros.split()).rstrip(",")
                membros.append(f"{metodo}({parametros})" + (f": {retorno.strip()}" if retorno else ""))
        classes[nome] = (pacote, fonte, membros, path.relative_to(ROOT).as_posix())
    uml = ["@startuml", "title Classes de produção e testes — geração de relatórios", "skinparam classAttributeIconSize 0"]
    mermaid = ["# Exercício 11 — Engenharia reversa", "", "Gerado por `python scripts/gerar-diagrama-testes.py` a partir do código Kotlin.",
               "As setas indicam referências a tipos encontrados no fonte. Recorte do caso de uso; não inclui todas as dependências externas.", "", "```mermaid", "classDiagram", "direction LR"]
    for nome, (pacote, _, membros, origem) in classes.items():
        estereotipo = "test" if nome.endswith("Test") else "production"
        uml += [f"' Fonte: {origem}", f'package "{pacote}{".tests" if estereotipo == "test" else ""}" {{', f"class {nome} <<{estereotipo}>> {{"]
        uml += [f"  {membro}" for membro in membros]
        uml += ["}", "}"]
        # Vista compacta para leitura no Markdown; assinaturas completas estão no PlantUML.
        mermaid.append(f"class {nome}")
    for nome, (_, fonte, _, _) in classes.items():
        for destino in classes:
            if destino != nome and re.search(rf"\b{re.escape(destino)}\b", fonte):
                uml.append(f"{nome} ..> {destino} : usa")
                mermaid.append(f"{nome} ..> {destino} : usa")
    uml.append("@enduml")
    mermaid += ["```", "", "## Fontes", ""]
    mermaid += [f"- `{dados[3]}`" for dados in classes.values()]
    destino = ROOT / "docs/entrega"
    destino.mkdir(parents=True, exist_ok=True)
    (destino / "engenharia-reversa.puml").write_text("\n".join(uml) + "\n", encoding="utf-8")
    (destino / "engenharia-reversa.md").write_text("\n".join(mermaid) + "\n", encoding="utf-8")
    print(f"Geradas {len(classes)} classes em docs/entrega/engenharia-reversa.[puml|md]")


if __name__ == "__main__":
    main()
