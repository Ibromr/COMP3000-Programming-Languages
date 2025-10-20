
Main processes determining precipitation reaching the river

## Atmosphere/vegetation

* Interception: Portion caught in leaves/canopy and evaporated.

* Direct evaporation: From wet surfaces after rainfall.

## Surface

* Depression storage: Water collected in micro-depressions.

* Infiltration and infiltration capacity: Water entering the soil.

* Hortonian infiltration-excess runoff: Rainfall intensity > infiltration capacity.

* Saturation-excess runoff: Soil saturated; additional rainfall becomes surface runoff.

* Overland flow: Influenced by slope, roughness, land cover.

* Impervious areas: Roads, roofs, etc., produce immediate runoff.

* Urban drainage: Rapid conveyance via storm drains, channels, pipes.
  

## Soil layer

* Infiltration → soil moisture.

* Percolation → water moving deeper underground.

* Interflow: Lateral subsurface flow through shallow depths along slope.

* Transpiration and total evapotranspiration (ET).


## Groundwater

* Recharge → aquifer.

* Baseflow: Slow, steady discharge to the river.

* Bank storage.

## Channel/useful storages

* Routing in the channel, wave propagation, and attenuation.

* Floodplain storage and return flow.

* Dam/reservoir operations, spillways.

* Water withdrawals, irrigation return flow.

## Cryosphere/climate

* Snow and ice accumulation and melt.

* Frozen ground (reduces infiltration).

* Temperature, wind: Affect ET and melting.

## Initial/area characteristics

* Antecedent moisture condition.

* Soil type/permeability, macropores.

* Basin size/shape, drainage density, slope.

* Land use/cover (forest, agriculture, urban).

## Simple model representation suggestions (as parameters/functions in your DSL):

* Losses: interception_f, ET_f, initial_abstraction S; simple runoff coefficient c.

* Infiltration: infil_cap or single-parameter loss models like Curve Number/Green-Ampt.

* Surface/channel routing: delay kernel k[0..10] or distribution parameters with “~”.

* Baseflow: exponential recession r (e.g., baseflow = B0 * r^t).

* Storage/routing: single/double linear reservoirs K, Muskingum-type (K, X).

* Impervious fraction: imp_f; effective rainfall = (1 − loss) * rainfall.



Kısaca: Yağışın nehre ulaşmasını belirleyen ana süreçler

- Atmosfer/bitki örtüsü
  - Taç tutulumu (interception): Yaprak/kanopide tutulup buharlaşan kısım.
  - Doğrudan buharlaşma: Yağış sonrası ıslak yüzeylerden.

- Yüzey
  - Çöküntü depolaması (depression storage): Mikro-çukurlarda biriken su.
  - Sızma (infiltration) ve sızma kapasitesi: Toprağa giren su.
  - İnfiltrasyon-aşımı taşkın akışı (Hortonian): Yağış şiddeti > sızma kapasitesi.
  - Doygunluk-aşımı taşkın akışı: Zemin doygun, ekstra yağış yüzey akışına dönüşür.
  - Yüzey akışı (overland flow): Eğim, pürüzlülük, arazi örtüsü etkiler.
  - Geçirimsiz alanlar: Yol, çatı vb. anında akış üretir.
  - Kentsel drenaj: Mazgal, kanal, boru sistemleriyle hızlı iletim.

- Toprak katmanı
  - Toprağa giriş (infiltration) → toprak nemi.
  - Süzülme (percolation) → yeraltına sızma.
  - Yanal ara akış (interflow): Sığ derinliklerden eğim yönünde akış.
  - Terleme (transpirasyon) ve toplam evapotranspirasyon (ET).

- Yeraltı suyu
  - Beslenim (recharge) → akifer.
  - Taban akışı (baseflow): Nehre yavaş ve sürekli deşarj.
  - Kıyı/ban k depolaması (bank storage).

- Kanal/faydalı depolar
  - Kanal içinde yönlendirme (routing), dalga yayılımı ve sönüm.
  - Taşkın ovası depolaması ve geri dönüşü.
  - Baraj/rezervuar işletmesi, savaklar.
  - Su çekimi/kaptaj, sulama dönüş akışı.

- Kriyosfer/iklim
  - Kar-buz birikimi ve erimesi (snowmelt).
  - Donmuş zemin (infiltrasyonu azaltır).
  - Sıcaklık, rüzgar: ET ve erimeyi etkiler.

- Başlangıç/alan özellikleri
  - Ön nem durumu (antecedent moisture).
  - Toprak tipi/iletkenlik, makrogözenekler.
  - Havza alanı/şekli, drenaj yoğunluğu, eğim.
  - Arazi kullanımı/örtüsü (orman, tarım, şehir).

Modelde basit temsil önerileri (DSL’inizde parametre/fonksiyon olarak)
- Kayıplar: interception_f, ET_f, initial_abstraction S; basit runoff katsayısı c.
- Sızma: infil_cap veya CurveNumber/Green-Ampt benzeri tek parametreli kayıp modeli.
- Yüzey/kanal yönlendirme: gecikme çekirdeği k[0..10] veya “~” ile dağılım parametreleri.
- Taban akışı: üstel resesyon r (ör. baseflow = B0 * r^t).
- Depolama/yönlendirme: tek/çift lineer rezervuar K, Muskingum benzeri (K, X).
- Geçirimsiz oran: imp_f; etkin yağış = (1−kayıp) * rainfall.
