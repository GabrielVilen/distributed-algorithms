Aufgabenbearbeitung mit Teachnet
================================

"Lieferumfang"
--------------

teachnet.jar 	- Ausführbare JAR-Datei mit GUI-Frontend
lib/ 		- von Teachnet benötigte Bibliotheken
vorlagen/	- Vorlagen für die Aufgabenbearbeitung
doc/		- Dokumentation (u.a. diese Datei)



Quickstart
----------

Vorweg eine kurze Zusammenfassung über die Verwendung von Teachnet
zur Aufgabenbearbeitung im Kurs Verteilte Algorithmen.

Schritt 1 - Java-Datei mit neuem Algorithmus erzeugen:

Im Verzeichnis "vorlagen" befindet sich ein Beispiel.

Es ist nicht unbedingt erforderlich die Dateien mit "javac" zu übersetzen,
obwohl dies zur genaueren Fehleranalyse erfolgen kann.


Schritt 2 - Teachnet-GUI starten:

Nach der Bearbeitung der Source-Dateien sollten diese per GUI-Frontend
kontrolliert werden.

Dazu ist zunächst die Teachnet-GUI zu starten, dies erfolgt mittels dem
Befehl:

java -jar teachnet.jar


Schritt 3 - Vorlagen-Sourcen laden:

Nach dem Starten der GUI, müssen zunächst die bearbeiteten Source-Dateien
übersetzt und die Klassen geladen werden.

Dies erfolgt im Menü "Classpath" mit dem Menüpunkt "Compile and load classes".
In dem erscheinenden Dateiauswahldialog ist lediglich der Ordner
auszuwählen, der die Java-Dateien enthält, und mit "Öffnen" bzw. "Open" zu bestätigen.

Bei Fehlern erscheint voerst nur ein schlichter Dialog, genauere Fehlersuche
also nur mit selber kompilieren.

Hinweis:
Wer die Dateien schon selber kompiliert hat kann die auch beim Start der 
Teachnet-GUI zum Klassenpfad hinzufügen, dann entfällt Schritt 3:

java -cp <Pfad zu den Klassen>:teachnet.jar teachnet/view/TeachnetFrame


Schritt 4 - Konfiguration laden:

Als nächstes ist eine Konfiguration für den Simulationsablauf auszuwählen.
Diese sind in Dateien spezifiert (Dokumentation in "doc/config-example.txt").

Eine der zur Aufgabe passenden Konfiguration muss jetzt im Menü "Simulation" mit
dem Menüpunkt "Load config" geladen werden. 

Es sollte ein nun ein Graph erscheinen. Die GUI versucht den Graph übersichtlich
anzuordnen, die Knoten können aber mittels ziehen mit der linken Maustaste
verschoben werden.


Schritt 5 - Ansicht auswählen:

Es gibt im Menü "View" verschiedene Ansichten zur Auswahl. Diese
können ausgewählt werden um bestimmte Algorithmusabläufe besser zu erkennen.

Das mitgelieferte Beispiel zeigt, was derzeit mit der "generischen Sicht"
möglich ist.


Schritt 6 - Simulation durchführen:

Mit den Tasten auf der Werkzeugleiste kann die Simulation nun gesteuert werden:

(von links nach rechts)

1. Zurücksetzen mit neuen Zufallsgenerator (Zurückspulen-Symbol)

Spult die Simulation zum Anfang zurück und erzeugt einen neuen
Zufallsgenerator, der bewirkt, dass sich die Simulation nicht
exakt wiederholt.

2. Zurücksetzen mit gleichem Zufallsgenerator (Track-Zurück-Symbol)

Spult die Simulation zum Anfang zurück und behält den Zufallsgenerator
bei, was bewirkt, dass sich die Simulation wieder exakt genauso verhält
wie beim Durchgang davor.

3. Starten/Pausieren (Play/Pause-Symbol)

Startet bzw. Pausiert (Symbol wechselt) die Simulation. Falls der Algorithmus
terminiert (keine Nachrichten mehr unterwegs) läuft die Simulation bis zum
Ende durch und pausiert dann automatisch.


Implementierung von Algorithmen
-------------------------------

Als Basis für alle Algorithmen in Teachnet dient die Klasse 
teachnet.algorithm.BasicAlgorithm, die abzuleiten ist.

Die einzige Vorgabe für die abgeleitete Klasse ist, dass diese nur einen
Default-Konstruktor (ohne Parameter) haben darf.


Methoden die implementiert werden müssen:


void initiate()

Diese Methode dient dazu den Algorithmus zu starten und kann auf einem oder
mehreren Knoten ausgeführt werden. Wann und wo sie ausgeführt wird, wird durch
die verwendete Konfigurationsdatei vorgegeben.


void receive(int interf, Object message)

Diese Methode wird beim Eingehen einer Nachricht ausgeführt und bildet den Kern
des Simulationsmodells. Innerhalb dieser Methode kann der Algorithmus eingehende
Nachrichten analysieren und durch versenden von Nachrichten reagieren (s.u.).
Das Simulationsmodell geht davon aus, dass die Ausführungszeit dieser Methode
(und auch aller anderen Methoden) 0 beträgt, es sollte also auf keinen Fall
z.B. mit Thread.sleep gewartet werden.
Die wahre Ausführungszeit aller Methoden kann natürlich beliebig lang sein (z.B.
für aufwendige Berechnungen, die Simulation pausiert jedoch bei jedem Methodenaufruf
bis dieser terminiert.


Methoden die überschrieben werden können:


void setup(Map<String, Object> config)

Da die Klasse keine Parameter über ihren Konstruktor erhalten kann, müssen evtl.
vorhandene Algorithmusparameter über die Methode übergeben werden. 
Wie diese Parameter heißen und welche Werte sie haben kann über die Konfigurationsdatei
festgelegt werden. 
In dieser Methode dürfen noch KEINE Nachrichten verschickt werden. 
Sie wird vor Beginn der Simulation für jeden Knoten einmalig aufgerufen.
Wird diese Methode nicht überschrieben (weil z.B. keine Parameter benötigt werden)
wird eine leere Implementierung verwendet.


void timeout(Object message)

Diese Klasse dient als Callback-Methode für Timeouts (s.u.). Falls keine Timeouts
verwendet werden muss sie nicht überschrieben werden.


Methoden die von BasicAlgorithm zur Verfügung gestellt werden:


int checkInterfaces()

Diese Methode gibt die Anzahl der Interfaces des Knotens auf dem der Algorithmus
läuft aus. Die Interfaces sind von 0 bis n-1 numeriert, wobei n der Rückgabewert von
dieser Methode ist.


void send(int interf, Object message)

Diese Methode bildet die einzige Möglichkeit im verwendeten Simulationsmodell
mit anderen Knoten zu kommunizieren.
Es wird eine Nachricht (tatsächliche Klasse beliebig) über das angegebene Interface
versendet. Der Wert für das Interface muss sich im Rahmen der Werte bewegen, die durch
checkInterfaces vorgegeben werden (s.o.).
Je nach Netzwerktopologie erreicht diese Nachricht von 0 Knoten (keine anderen Knoten 
über dieses Interface verbunden) bis n Knoten (n andere Knoten sind mit diesem
Interface verbunden).
WICHTIG: Da keine Anforderungen an die Klasse des versendeten Objekts gestellt werden
(insbesondere kann es nicht serialisiert oder geklont werden) MUSS das versendete
Objekt nach dem versenden als unveränderlich angesehen werden, insbesondere
sollte das Objekt nicht verändert und erneut versendet werden.
Das Versenden einer Nachricht über alle Interfaces könnte beispielsweise so aussehen:

    for (int i = 0; i < checkInterfaces(); i++) {
        // WICHTIG: immer eine NEUE Nachricht senden
        send(i, new MyMessage());
    }


void setTimeout(double time, Object message)

Hiermit kann bspw. das Warten auf ein Ereignis simuliert werden. Diese Methode
sorgt dafür dass nach der angegebenen Zeit die Methode timeout mit dem Wert
message als Parameter aufgerufen wird.


Mehrere Schichten von Algorithmen
---------------------------------


Teachnet unterstützt die Simulation von mehren Schichten von Algorithmen
(Protokollschichten). Hierzu dient die Klasse teachnet.algorithm.LayerAlgorithm,
diese ist wie teachnet.algorithm.BasicAlgorithm zu verwenden und bietet folgende
Erweiterungen:


Methoden die implementiert werden müssen:


void forward(int interf, Object message)

Diese Methode ist das Analog zu receive und wird aufgerufen wenn die weiter
oben liegende Schicht send aufruft.


Methoden die von BasicAlgorithm zur Verfügung gestellt werden:


void handle(int interf, Object message)

Diese Methode ist das Analog zu send und wird verwendet um Nachrichten an die
weiter oben liegende Schicht zu senden. Hierbei wird bei der anderen Schicht
receive aufgerufen.


Dieses Verhalten ist für die Schichten transparent. Eine Schicht "weiß" also nicht
ob das Aufrufen von send direkt an das Interface des Knotens weitergeleitet wird
oder ob die Methode handle einer weiter unten liegenden Schicht aufgerufen wird.
Gleiches gilt für die andere Richtung - ein Aufruf von receive kann direkt vom Knoten
erfolgen oder durch die Methode forward einer anderen Schicht erfolgen.

Die oberste Schicht sollte also immer von der Klasse teachnet.algorithm.BasicAlgorithm
abgeleitet sein (client layer).

Eine Schicht die nur Nachrichten "durchschleift" könnte dann so aussehen:

	@Override
	public void forward(int interf, Object message) {
		send(interf, message);
	}

	@Override
	public void receive(int interf, Object message) {
		handle(interf, message);
	}


Algorithmen mit Routing
-----------------------


Als letzte Möglichkeit bietet Teachnet noch eine kleine Erweiterung an, die mit
der Klasse teachnet.algorithm.RoutingAlgorithm realisiert wird.
Diese verhält sich wie teachnet.algorithm.LayerAlgorithm und erfordert zusätzlich
das implementieren einer Methode:


int getInterfaces()

Wird aufgerufen wenn die weiter oben liegende Schicht checkInterfaces aufruft und wird
als Ergebnis für die obere Schicht verwendet.
Es ist also nun möglich die Anzahl der Interfaces die für die obere Schicht sichtbar
sind zu ändern (weniger oder mehr).
WICHTIG: Da davon ausgegangen wird, dass die Interfaces von 0 bis n-1 (n ist das
Ergebnis von checkInterfaces) durchnumeriert sind, muss dies beachtet werden.

Eine Schicht die das erste Interface (0) ausblendet könnte so aussehen 
(funktioniert nur bei 1+ Interfaces):

	@Override
	public int getInterfaces() {
		return checkInterfaces() - 1;
	}
    
	@Override
	public void forward(int interf, Object message) {
		send(interf + 1, message);
	}




