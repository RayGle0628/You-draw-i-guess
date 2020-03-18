package messaging;

/**
 * Commands that can be used as message headers.
 */
public enum Command {
    /**
     * LOGIN - Used in messages containing log in information from the client to the server.
     */
    LOGIN,
    /**
     * LOGOUT - Indicates to the server that the client wants to close their connection. This message is returned from
     * the server to the client so that the ClientListener can break out of it's listening loop and end.
     */
    LOGOUT,
    /**
     * CREATE_ACCOUNT - Used in messages containing new account information from the client to the server.
     */
    CREATE_ACCOUNT,
    /**
     * CHAT_MESSAGE_FROM_CLIENT - Used in messages that carry chat entered by a client into their chat box to the
     * server to be distributed to other players.
     */
    CHAT_MESSAGE_FROM_CLIENT,
    /**
     * CHAT_MESSAGE_TO_CLIENT - Used in messages that carry chat messages from the server back to the client to be
     * displayed.
     */
    CHAT_MESSAGE_TO_CLIENT,
    /**
     * REQUEST_GAME_INFO - Requests information about a game from the server when a user joins a room to populate the
     * GUI.
     */
    REQUEST_GAME_INFO,
    /**
     * USERS_IN_ROOM - Used in messages that carry a list of users in a specific game room to the client from the
     * server to be displayed. Sent in response to REQUEST_GAME_INFO and changes in users in the room.
     */
    USERS_IN_ROOM,
    /**
     * EXIT_ROOM - Sent to the server when a client leaves a room so the user can be removed from the rooms pool of
     * players.
     */
    EXIT_ROOM,
    /**
     * DRAW_PATH_FROM_CLIENT - Used in messages that carry a path object as drawn by a client to the server to be
     * distributed to other players.
     */
    DRAW_PATH_FROM_CLIENT,
    /**
     * DRAW_PATH_TO_CLIENT - Used in messaged that carry a path object from the server to be drawn on the client screen.
     */
    DRAW_PATH_TO_CLIENT,
    /**
     * START_DRAWING - Sent from server to client to enable drawing on their canvas.
     */
    START_DRAWING,
    /**
     * STOP_DRAWING - Sent from server to client to disable drawing on their canvas.
     */
    STOP_DRAWING,
    /**
     * CLEAR_CANVAS - Sent by the server to clients to force the canvas to be cleared. Can also be sent from the
     * drawer to the server to force every user to have their canvas cleared.
     */
    CLEAR_CANVAS,
    /**
     * GET_SCORES - Request from a client when they log in to retrieve the top 10 players for the leader board.
     */
    GET_SCORES,
    /**
     * RETURN_SCORES - Message that contains requested leader board scores to be returned to the client.
     */
    RETURN_SCORES,
    /**
     * GET_MY_SCORE - Request from a client when they log in to retrieve their ranking for the leader board.
     */
    GET_MY_SCORE,
    /**
     * RETURN_MY_SCORE - Message that contains requested user ranking to be displayed on the leader board.
     */
    RETURN_MY_SCORE,
    /**
     * GET_ROOMS - Request from a client when they log in to retrieve the list of game rooms available on the server.
     */
    GET_ROOMS,
    /**
     * RETURN_ROOMS - Message that contains requested game rooms to be displayed on the users home scene.
     */
    RETURN_ROOMS,
    /**
     * JOIN_ROOM - Request from a client to join a specific room.
     */
    JOIN_ROOM,
    /**
     * CONFIRM_JOIN_ROOM - Returned from server in response to successfully joining a room.
     */
    CONFIRM_JOIN_ROOM,
    /**
     * REJECT_JOIN_ROOM - Returned from server in response to failing to join a room.
     */
    REJECT_JOIN_ROOM
}
