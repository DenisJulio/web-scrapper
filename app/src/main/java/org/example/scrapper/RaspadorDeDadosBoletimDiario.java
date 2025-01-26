package org.example.scrapper;

import java.time.LocalDate;
import java.util.List;

public interface RaspadorDeDadosBoletimDiario {

	public List<DadosRaspadosSobreMercado> raspeDadosSobreMercados();

	public List<LocalDate> raspeDiasDeCotacaoParaMercado(String siglaDeMercado);

	public void raspeBoletimDiarioParaMercado(String siglaDeMercado, List<LocalDate> diasDeCotacao,
			ManipuladorDeDadosRaspados<DadosRaspadosDeBoletimDiario> handler);
}
