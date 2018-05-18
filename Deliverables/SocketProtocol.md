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
    "method": "addPlayer",
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
    "method": "updateWaitingPlayers",
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
    "method": "subscribeToWRTimer",
    "arg": null
}
```

#### Server -> Client

```
{
    "method": "subscribeToWRTimer",
    "result": <bool>
}
```

### Timer tick

#### Server -> Client

```
{
    "method": "wrTimerTick",
    "tick": <int>
}
```

### Remove Player

#### Client -> Server

## Game

### Game started message

#### Server -> Client

```
{
    "method": "gameStarted",
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
    ],
    "activePlayer": <nickname: string>
}
```

### Subscribe to timer's updates

#### Client -> Server

```
{
    "playerID": <uuid: string>,
    "method": "subscribeToGameTimer",
    "arg": null
}
```

#### Server -> Client

```
{
    "method": "subscribeToGameTimer",
    "result": <bool>
}
```

### Timer tick

#### Server -> Client

```
{
    "method": "gameTimerTick",
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
- [Draft Pool](#draft-pool)

### Players list

#### Server -> Client

```
{
    "method": "players",
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
    "method": "nextTurn",
    "currentRound": <int>,
    "roundOver": <bool>,
    "gameOver": <bool>,
    "activePlayer": <nickname: string>
}
```

### Final scores

#### Server -> Client

```
{
    "method": "finalScores",
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
    "method": "publicObjectiveCards",
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
    "method": "toolCards",
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
    "method": "favorTokens",
    "favorTokens": {
        <nickname: string>: <favorTokens: int>
    }
}
```

### Window pattern

#### Server -> Client

```
{
    "method": "windowPattern",
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
    "method": "roundTrackDice",
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
    "method": "draftPool",
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
    "method": "placeDie",
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
    "method": "useToolCard",
    "result": <bool>
}
```
