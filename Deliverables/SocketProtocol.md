# Socket Protocol

<!-- TOC depthFrom:2 depthTo:3 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Waiting Room](#waiting-room)
	- [Inserimento di un nuovo giocatore](#inserimento-di-un-nuovo-giocatore)
	- [Aggiornamento della lista dei giocatori in attesa](#aggiornamento-della-lista-dei-giocatori-in-attesa)
- [Game](#game)
	- [Messaggio di inizio gioco](#messaggio-di-inizio-gioco)
	- [Selezione pattern](#selezione-pattern)
	- [Lista dei giocatori](#lista-dei-giocatori)
	- [Conclusione del turno](#conclusione-del-turno)
	- [Punteggi finali](#punteggi-finali)
	- [Carte obiettivo pubblico](#carte-obiettivo-pubblico)
	- [Carte strumento](#carte-strumento)
	- [Segnalini favore](#segnalini-favore)
	- [Pattern](#pattern)
	- [Dadi sul tracciato dei round](#dadi-sul-tracciato-dei-round)
	- [Riserva](#riserva)
- [Mosse del giocatore](#mosse-del-giocatore)
	- [Posizionamento di un dado](#posizionamento-di-un-dado)
	- [Uso di una carta strumento](#uso-di-una-carta-strumento)

<!-- /TOC -->

## Waiting Room

### Inserimento di un nuovo giocatore

#### Client -> Server

```
{
    "player": <nickname: string>,
    "method": "addPlayer",
    "arg": null
}
```

#### Server -> Client

```
{
    "msgType": "addPlayer",
    "logged": <bool>,
    "UUID": <string>,
    "players": [
        <nickname: string>,
        ...
    ]
}
```

### Aggiornamento della lista dei giocatori in attesa

#### Server -> Client

```
{
    "msgType": "updateWaitingPlayers",
    "waitingPlayers": [
        <nickname: string>,
        ...
    ]
}
```

## Game

### Messaggio di inizio gioco

#### Server -> Client

```
{
    "msgType": "gameStarted",
    "privateObjectiveCard": {
        "name": <string>,
        "description": <string>,
        "victoryPoints": <int>
    }
    "windowPatterns": [     // 4 patterns
        {
            "difficulty": <int>,
            "grid": [
                {
                    "cellColor": <string>,
                    "cellValue": <int>,
                    "die": {
                        "color": <string>,
                        "value": <int>
                    }
                },
                ...
            ]
        },
        ...
    ]
}
```

### Selezione pattern

#### Client -> Server

```
{
    "playerID": <uuid: string>,
    "method": "choosePattern",
    "arg": {
        "patternIndex": <int>
    }
}
```

Dopo questa richiesta il server manda a ciascun client (nel seguente ordine):

- [Lista dei giocatori](#lista-dei-giocatori)
- [Carte strumento](#carte-strumento)
- [Carte obiettivo pubblico](#carte-obiettivo-pubblico)
- [Pattern](#pattern) degli altri giocatori
- Messaggio di [conclusione del turno](#conclusione-del-turno) per passare al turno 1
- [Dadi della riserva](#riserva)

### Lista dei giocatori

#### Response

```
{
    "msgType": "players",
    "players": [
        <nickname: string>,
        ...
    ]
}
```

### Conclusione del turno

#### Client -> Server

```
{
    "playerID": <uuid: string>,
    "method": "nextTurn",
    "arg": null
}
```

#### Response

```
{
    "msgType": "nextTurn",
    "currentRound": <int>,
    "roundOver": <bool>,
    "gameOver": <bool>,
    "activePlayer": <nickname: bool>
}
```

### Punteggi finali

#### Server -> Client

```
{
    "msgType": "finalScores",
    "finalScores": {
        <nickname: string>: <score: int>,
        ...
    }
}
```

### Carte obiettivo pubblico

#### Server -> Client

```
{
    "msgType": "publicObjectiveCards",
    "cards": [
        {
            "name": <string>,
            "description": <string>,
            "victoryPoints": <int>
        },
        ...
    ]
}
```

### Carte strumento

#### Server -> Client

```
{
    "msgType": "toolCards",
    "cards": [
        {
            "name": <string>,
            "description": <string>,
            "used": <bool>
        }
    ]
}
```

### Segnalini favore

#### Server -> Client

```
{
    "msgType": "favorTokens",
    "favorTokens": {
        <nickname: string>: <favorTokens: int>
    }
}
```

### Pattern

#### Server -> Client

```
{
    "msgType": "windowPattern",
    "player": <nickname: string>,
    "windowPattern": {
        "difficulty": <int>,
        "grid": [
            {
                "cellColor": <string>,
                "cellValue": <int>,
                "die": {
                    "color": <string>,
                    "value": <int>
                }
            },
            ...
        ]
    }
}
```

### Dadi sul tracciato dei round

#### Server -> Client

```
{
    "msgType": "roundTrackDice",
    "dice": [
        {
            "color": <string>,
            "value": <int>
        },
        ...
    ]
}
```

### Riserva

#### Server -> Client

```
{
    "msgType": "draftPool",
    "dice": [
        {
            "color": <string>,
            "value": <int>
        },
        ...
    ]
}
```

## Mosse del giocatore

### Posizionamento di un dado

#### Request

```
{
    "playerID": <uuid: string>,
    "method": "placeDie",
    "arg": {
        "draftPoolIndex": <int>,
        "x": <int>,
        "y": <int>
    }
}
```

#### Response

```
{
    "msgType": "placeDie",
    "result": <bool>
}
```

### Uso di una carta strumento

#### Request

```
{
    "playerID": <uuid: string>,
    "method": "useToolCard",
    "arg": {
        "cardIndex": <int>,
        "data": <object>      // Different data is required for each ToolCard
    }
}
```

#### Response

```
{
    "msgType": "useToolCard",
    "result": <bool>
}
```
