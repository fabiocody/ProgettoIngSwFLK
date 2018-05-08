# Test effettuati


### Dadi
- Valore di ogni dado compreso tra 1 e 6
- Colore di ogni dado tra quelli consentiti
- Il generatore genera effettivamente i dadi
- Non più di 90 dadi generati
- 18 dadi per ogni colore

### Carte obiettivo
- Genera esattamente 3 carte (pubbliche) diverse
- Genera tante carte (private) quante sono i giocatori, tutte diverse
- Calcolo dei punteggi corretto

### Carte strumento
- Genera esattamente 3 carte diverse

### Window Pattern
- Difficoltà compresa tra 3 e 6 inclusi
- patternNumber compreso tra 0 e 23 inclusi
- Facciate di una stessa carta accoppiate
- Ogni facciata composta da 20 celle
- Vincolo di valore per ogni cella inesistente o compreso tra 1 e 6 inclusi
- Vincolo di colore per ogni cella inesistente o relativo a un colore ammesso
- Verifica che i vincoli di piazzamento funzionino

### Player
- Getter e setter relativi al Window Pattern e alla carta obiettivo privato rispettano le tempistiche

### Round Track
- Incrementa correttamente il round e non supera 10

### TurnManager
- Corretta alternanza di 2 giocatori
- Corretta alternanza di 3 giocatori
- Corretta alternanza di 4 giocatori

### Waiting Room
- Corretta creazione di una partita una volta raggiunti 4 giocatori.