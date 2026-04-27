import apiClient from "./apiClient";

function buildResourcePath(resource, idValues = {}) {
  if (!resource.idFields || resource.idFields.length === 0) {
    return resource.path;
  }

  const missingField = resource.idFields.find((field) => idValues[field] === undefined || idValues[field] === null || idValues[field] === "");

  if (missingField) {
    throw new Error(`Campo de identificacao ausente: ${missingField}`);
  }

  const suffix = resource.idFields.map((field) => encodeURIComponent(idValues[field])).join("/");
  return `${resource.path}/${suffix}`;
}

export function normalizePayload(resource, values, mode = "create") {
  const payload = {};

  resource.fields.forEach((field) => {
    if (mode === "update" && field.createOnly) {
      return;
    }

    const rawValue = values[field.name];

    if (rawValue === undefined || rawValue === null || rawValue === "") {
      return;
    }

    if (field.type === "number") {
      payload[field.name] = Number(rawValue);
      return;
    }

    if (field.type === "boolean") {
      payload[field.name] = rawValue === true || rawValue === "true";
      return;
    }

    if (field.type === "decimal") {
      payload[field.name] = Number(rawValue);
      return;
    }

    payload[field.name] = rawValue;
  });

  return payload;
}

export async function listResources(resource, filters = {}) {
  const params = {};

  if (resource.filters) {
    resource.filters.forEach((filter) => {
      const value = filters[filter.name];
      if (value !== "" && value !== undefined && value !== null) {
        params[filter.name] = value;
      }
    });
  }

  const response = await apiClient.get(resource.path, { params });
  return response.data;
}

export async function createResource(resource, payload) {
  const response = await apiClient.post(resource.path, payload);
  return response.data;
}

export async function updateResource(resource, idValues, payload) {
  const path = buildResourcePath(resource, idValues);
  const response = await apiClient.put(path, payload);
  return response.data;
}

export async function deleteResource(resource, idValues) {
  const path = buildResourcePath(resource, idValues);
  await apiClient.delete(path);
}
