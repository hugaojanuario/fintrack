package br.com.fintrack.domain.market.client;

import java.util.List;

public record BrapiResponseDTO(List<BrapiQuoteDTO> results) {
}
