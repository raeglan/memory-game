package com.snatik.matches.model

/**
 * @param placementId The unique id for this exact card, i.e. where it is placed on the board.
 * @param pairId the id of the pair in question.
 * @param cardNumber which of the cards this represents on a pair, can be 0 or 1
 */
data class Card(val placementId: Int, val pairId: Int, val cardNumber: Int)