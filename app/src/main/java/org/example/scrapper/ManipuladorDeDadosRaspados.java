package org.example.scrapper;

@FunctionalInterface
public interface ManipuladorDeDadosRaspados<T> {

	void manipule(T dadosRaspados);
}
