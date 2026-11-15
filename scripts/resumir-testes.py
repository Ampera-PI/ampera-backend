"""Extrai evidências reais dos XMLs JUnit/JaCoCo gerados pelo Gradle (stdlib)."""
from pathlib import Path
import os
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "build/reports/evidencias-testes.md"
JACOCO = ROOT / "build/reports/jacoco/test/jacocoTestReport.xml"


def cobertura(elemento, tipo):
    contador = elemento.find(f"counter[@type='{tipo}']")
    if contador is None:
        return "n/a"
    cobertos, perdidos = int(contador.get("covered")), int(contador.get("missed"))
    total = cobertos + perdidos
    return f"{100 * cobertos / total:.2f}% ({cobertos}/{total})" if total else "n/a"


def main():
    suites = [ET.parse(p).getroot() for p in sorted((ROOT / "build/test-results/test").glob("TEST-*.xml"))]
    texto = ["# Evidências da execução", ""]
    if suites:
        totais = {k: sum(int(s.get(k, "0")) for s in suites) for k in ("tests", "failures", "errors", "skipped")}
        texto += ["| Testes | Falhas | Erros | Ignorados |", "| ---: | ---: | ---: | ---: |",
                  f"| {totais['tests']} | {totais['failures']} | {totais['errors']} | {totais['skipped']} |", "",
                  "| Classe | Testes | Falhas/erros | Ignorados |", "| --- | ---: | ---: | ---: |"]
        for suite in suites:
            falhas = int(suite.get("failures", "0")) + int(suite.get("errors", "0"))
            texto.append(f"| {suite.get('name')} | {suite.get('tests')} | {falhas} | {suite.get('skipped', '0')} |")
    else:
        texto.append("Sem XML JUnit: execução não comprovada por este resumo.")
    texto += ["", "## Cobertura", ""]
    if JACOCO.exists():
        report = ET.parse(JACOCO).getroot()
        texto += ["| Escopo | Instruções | Decisões | Métodos |", "| --- | --- | --- | --- |"]
        elementos = [("Projeto", report)]
        for package in report.findall("package"):
            elementos.append((package.get("name").replace("/", "."), package))
            for classe in package.findall("class"):
                if classe.get("name").rsplit("/", 1)[-1] in {
                    "RelatorioService", "ConsumoDoPeriodo", "PrevisorDeConsumo", "CalculadoraDeCusto", "Relatorio", "Donos"
                }:
                    elementos.append((classe.get("name").replace("/", "."), classe))
        for nome, elemento in elementos:
            texto.append(f"| {nome} | {cobertura(elemento, 'INSTRUCTION')} | {cobertura(elemento, 'BRANCH')} | {cobertura(elemento, 'METHOD')} |")
        texto += ["", "### Métodos do caso de uso", "", "| Classe / método (descritor JVM) | Instruções | Decisões |", "| --- | --- | --- |"]
        for package in report.findall("package"):
            for classe in package.findall("class"):
                nome = classe.get("name").rsplit("/", 1)[-1]
                if nome not in {"RelatorioService", "ConsumoDoPeriodo", "PrevisorDeConsumo", "CalculadoraDeCusto"}:
                    continue
                for metodo in classe.findall("method"):
                    assinatura = f"{nome}.{metodo.get('name')}{metodo.get('desc')}".replace("|", "\\|")
                    texto.append(f"| `{assinatura}` | {cobertura(metodo, 'INSTRUCTION')} | {cobertura(metodo, 'BRANCH')} |")
        texto += ["", "Métodos sintéticos, construtores e corrotinas podem aparecer com nomes JVM. Consulte também o HTML por classe."]
    else:
        texto.append("Sem XML JaCoCo: cobertura ainda não comprovada por este resumo.")
    resultado = "\n".join(texto) + "\n"
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text(resultado, encoding="utf-8")
    if os.environ.get("GITHUB_STEP_SUMMARY"):
        with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as resumo:
            resumo.write(resultado)
    print(resultado)


if __name__ == "__main__":
    main()
