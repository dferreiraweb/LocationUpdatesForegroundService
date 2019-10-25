# LocationUpdatesForegroundService

Este projeto segue o exemplo de https://github.com/android/location-samples

Em complemento este projeto utiliza o Retrofit para enviar parametros de localização a cada 10 segundos para uma URL no beeceptor.

O serviço já funciona com a tela do celular desligada, mas caso a aplicação seja encerrada então o serviço também é encerrado.

Isso acontece nas versões acima do Android 8. Então será necessário gerar mais atualizações para que o código possa funcionar em diferentes versões do Android.
