# Socket Protocol

<!-- TOC depthFrom:2 depthTo:3 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Waiting Room](#waiting-room)
	- [Add new player](#add-new-player)
	- [Update waiting players list](#update-waiting-players-list)
	- [Subscribe to timer's updates](#subscribe-to-timers-updates)
	- [Timer tick](#timer-tick)
- [Game](#game)
	- [Game started message](#game-started-message)
	- [Subscribe to timer's updates](#subscribe-to-timers-updates)
	- [Timer tick](#timer-tick)
	- [Pattern selection](#pattern-selection)
	- [Players list](#players-list)
	- [End turn](#end-turn)
	- [Final scores](#final-scores)
	- [Public objective cards](#public-objective-cards)
	- [Tool cards](#tool-cards)
	- [Favor tokens](#favor-tokens)
	- [Window pattern](#window-pattern)
	- [Dice on Round Track](#dice-on-round-track)
	- [Draft Pool](#draft-pool)
- [Player's moves](#players-moves)
	- [Die placement](#die-placement)
	- [Tool card usage](#tool-card-usage)

<!-- /TOC -->

## Waiting Room

### Add new player

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

### Update waiting players list

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

### Subscribe to timer's updates

#### Client -> Server

```
{
    "playerID": <uuid: string>,
    "method": "registerWRTimer",
    "arg": null
}
```

#### Server -> Client

```
{
    "msgType": "registerWRTimer",
    "result": <bool>
}
```

### Timer tick

#### Server -> Client

```
{
    "msgType": "wrTimerTick",
    "tick": <int>
}
```

## Game

### Game started message

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

### Subscribe to timer's updates

#### Client -> Server

```
{
    "playerID": <uuid: string>,
    "method": "registerGameTimer",
    "arg": null
}
```

#### Server -> Client

```
{
    "msgType": "registerGameTimer",
    "result": <bool>
}
```

### Timer tick

#### Server -> Client

```
{
    "msgType": "gameTimerTick",
    "tick": <int>
}
```

### Pattern selection

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

After this request, the server will send to each client the following information:

- [Players list](#players-list)
- [Tool cards](#tool-cards)
- [Public objective cards](#public-objective-cards)
- [Window pattern](#window-pattern)
- [End turn](#end-turn) message used to determine next active player
- [Draft Pool](#draft-pool)

### Players list

#### Server -> Client

```
{
    "msgType": "players",
    "players": [
        <nickname: string>,
        ...
    ]
}
```

### End turn

#### Client -> Server

```
{
    "playerID": <uuid: string>,
    "method": "nextTurn",
    "arg": null
}
```

#### Server -> Client

```
{
    "msgType": "nextTurn",
    "currentRound": <int>,
    "roundOver": <bool>,
    "gameOver": <bool>,
    "activePlayer": <nickname: bool>
}
```

### Final scores

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

### Public objective cards

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

### Tool cards

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

### Favor tokens

#### Server -> Client

```
{
    "msgType": "favorTokens",
    "favorTokens": {
        <nickname: string>: <favorTokens: int>
    }
}
```

### Window pattern

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

### Dice on Round Track

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

### Draft Pool

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

## Player's moves

### Die placement

#### Client -> Server

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

#### Server -> Client

```
{
    "msgType": "placeDie",
    "result": <bool>
}
```

### Tool card usage

#### Client -> Server

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

#### Server -> Client

```
{
    "msgType": "useToolCard",
    "result": <bool>
}
```
