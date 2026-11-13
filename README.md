# Progetto-Kadrija-Modellazione

**SUPERNATURAL RPG** è un gioco a turni ispirato all’universo di *Supernatural*. Il giocatore guida un gruppo di cacciatori attraverso tre livelli, affronta diversi nemici e utilizza armi, pozioni e abilità per superare gli incontri.

Il progetto è sviluppato in Java, utilizza Gradle per la compilazione e i test, e offre un’interfaccia grafica realizzata con JavaFX. È possibile avviare una nuova partita, continuare una sessione già aperta, salvare la partita e riprenderla in un secondo momento.

## Come eseguire il progetto

### Prerequisiti

- Java 25
- Git, per clonare il repository

Non è necessario installare Gradle manualmente: il progetto include il **Gradle Wrapper**.

### Istruzioni

Apri un terminale e clona il repository:

```bash
git clone https://github.com/SuloKadrija/Progetto-Kadrija-Modellazione.git
cd Progetto-Kadrija-Modellazione
```

### Build del progetto

```bash
./gradlew build
```

Il comando compila il progetto ed esegue i test automatici.

### Esecuzione


```bash
./gradlew run
```

Il comando avvia l’interfaccia grafica del gioco.

## Come si gioca

All’avvio è possibile scegliere una delle seguenti opzioni:

- **Nuova partita**: avvia una partita dal primo livello.
- **Continua**: riprende la sessione ancora aperta in memoria.
- **Carica**: ripristina una partita precedentemente salvata.
- **Esci**: chiude l’applicazione.

Durante il combattimento, ogni eroe può:

- Attaccare un nemico, scegliendo bersaglio e arma.
- Usare una pozione, se ferito e in possesso di una pozione.
- Passare il turno, soltanto se non ha azioni disponibili.

Dopo che tutti gli eroi hanno agito, i nemici vivi effettuano i loro attacchi. Quando tutti i nemici di un incontro sono sconfitti, il gruppo recupera la salute e avanza al prossimo incontro.

Il gioco comprende tre livelli:

1. Fantasma, vampiro e wendigo.
2. Demoni, demone dagli occhi gialli e Lucifero.
3. Leviatani, angeli e avversario finale.

## Salvataggi

La partita può essere salvata manualmente dal menu di combattimento tramite il pulsante **Salva partita**.

Il salvataggio viene creato nel file:

```text
saves/partita.json
```

Il file conserva la posizione nella partita, il turno corrente, la salute di eroi e nemici, gli eroi sbloccati e gli inventari. Il salvataggio non viene incluso nel repository Git, perché è un file locale personale.

Per riprendere una partita su un altro computer, copia manualmente `saves/partita.json` nella cartella `saves` del progetto clonato, poi seleziona **Carica** dall’applicazione.

## Test

Per eseguire soltanto i test automatici:

```bash
./gradlew test
```

I test verificano il funzionamento della logica di combattimento, della sessione, della preparazione dei livelli, della persistenza dei salvataggi e delle interfacce di gioco.

## Struttura del progetto

Il codice sorgente è organizzato nel package:

```text
it.unicam.cs.mpgc.rpg129131
```

Le principali aree del progetto sono:

- `combat`: azioni, danni, eventi e gestione delle singole azioni di combattimento.
- `game`: sessione, stato della partita, livelli, preparazione e ciclo di gioco.
- `model`: eroi, nemici, oggetti, gruppi, incontri e livelli.
- `persistence`: salvataggio e caricamento delle partite in formato JSON.
- `ui`: interfaccia grafica JavaFX e vista console.

## Uso di strumenti di AI

l’AI è stata utilizzata per:

- Chiarire errori di compilazione e messaggi degli strumenti di sviluppo.
- Comprendere concetti relativi a Java, Gradle, Git, JavaFX e test automatici.
- Attività di debugging spiegata passo passo per farmi comprendere gli errori commessi.
- Generare le classi test, successivamente riviste, adattate e verificate manualmente.
- Generare i commenti all’interno del codice già scritto da me, successivamente rivisti, adattati e verificati manualmente.

