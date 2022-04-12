# IJA - Úkol 3

## Členové týmu
Tran Thanh Quang M. - xtrant02

Lukáš Fuis - xfuisl00

##Překlad a spuštění
Přeložení a spuštění projektu proběhne po zadání příkazu `ant run`, zároveň se i s příkazem vygeneruje dokumentace

##Generování dokumentace
Generování dokumentace proběhne po zadání příkazu `ant doc`

##Funkce aplikace
Po spuštění aplikace je uživatel v módu Select a má následující možnosti:

Může vytvořit třídu pomocí tlačítka Add. Třída po vytvoření obsahuje svůj název a dva atributy.

Název a atributy lze editovat jelikož se jedná o textové pole. Atributy lze i přidat, či odebrat pomocí kontextového menu, které se otevře stisknutím pravého tlačítka myši na daný atribut.
Nebo v módu Select kamkoliv ve třídě.

Může pomocí tlačítka Delete změnit mód myši na Delete.

Může pomocí tlačítka Connect změnit mód myši na Connect.

Může pomocí tlačítka Select změnit mó myši na Select.

###Mód Select
V módu Select může uživatel se třídami táhnout po obrazovce a přesouvat je. 

###Mód Connect
V módu Connect může uživatel následujícímí 2 kliknutími na různé třídy mezi nimi vytvořit vztah. Následně je mód změněn zpět na Select.

Opakované kliknutí na tlačítko Connect změní mód zpět na Select.
###Mód Delete
Kliknutí v módu Delete na třídu způsobí její smazání, a odstranění všech vztahů, které měla.

Opakované kliknutí na tlačítko Delete změní mód zpět na Select.