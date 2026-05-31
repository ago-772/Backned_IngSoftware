// ===== REQUESTS =====

interface TelemetryRequest {
  temperature: number;        // 0.0 – 100.0
  targetTemperature: number;  // 10.0 – 100.0
  waterLevel: number;         // 0.0 – 100.0
  createdAt: string;          // ISO 8601, ej: "2026-05-31T12:00:00Z"
}

// ===== RESPONSES =====

interface TelemetryResponse {
  id: number;
  temperature: number;
  targetTemperature: number;
  waterLevel: number;
  createdAt: string;
}

// ===== ENDPOINTS =====

// POST /telemetry → 201 | 400
type PostTelemetry = (body: TelemetryRequest) => Promise<TelemetryResponse>;

// GET /telemetry → 200
type GetTelemetry = () => Promise<TelemetryResponse[]>;

// GET /telemetry/latest → 200 | 404
type GetLatestTelemetry = () => Promise<TelemetryResponse | null>;