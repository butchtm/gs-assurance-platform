import axios, { isAxiosError, Method, ResponseType } from "axios";

interface Params {
  page?: number;
  pageSize?: number;
  [key: string]: string | number | undefined;
}

interface MakeRequestOptions {
  page?: number;
  pageSize?: number;
  filters?: URLSearchParams;
  rawResponse?: boolean;
  download?: string;
}

export class APIClient {
  static readonly BACKEND_ROOT_PATH = "/api";
  static readonly CLIENT_VERSION = "2023-04-19";

  public static downloadBlob(blobUrl: string, filename: string): void {
    const link = document.createElement("a");
    link.href = blobUrl;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(blobUrl);
  }

  private buildHeaders(data?: string | FormData): Record<string, string> {
    const headers: Record<string, string> = {
      "X-Gold-Standard-Api-Version": APIClient.CLIENT_VERSION,
      accept: "*/*",
    };
    const isFormData = data instanceof FormData;
    if (!isFormData) {
      headers["Content-Type"] = "application/json";
    }

    return headers;
  }

  private buildParams(options?: MakeRequestOptions): Params {
    const params: Params = {
      page: options?.page,
      size: options?.pageSize,
    };
    if (options?.filters) {
      options.filters.forEach((value, key) => {
        params[key] = value;
      });
    }

    return params;
  }

  private handleAttachmentResponse(
    response: { data: ArrayBuffer },
    contentDisposition?: string,
    options?: MakeRequestOptions,
  ): string | void {
    const blobUrl = window.URL.createObjectURL(new Blob([response.data], { type: "application/octet-stream" }));

    const filename = this.toFilenameIfPresent(contentDisposition);
    if (filename) {
      APIClient.downloadBlob(blobUrl, filename);
      return;
    }

    if (options?.download) {
      APIClient.downloadBlob(blobUrl, options.download);
      return;
    }

    return blobUrl;
  }

  private toFilenameIfPresent(contentDisposition?: string): string | undefined {
    if (!contentDisposition?.includes("filename=")) {
      return undefined;
    }
    return contentDisposition.split("filename=")[1];
  }

  private handleError(error: unknown, signal?: AbortSignal): void {
    if (signal?.aborted) {
      return;
    }

    if (isAxiosError(error)) {
      console.error("Error making request:", error.message);
      if (error.response?.status === 401) {
        // eslint-disable-next-line no-restricted-syntax
        window.location.href = "/NoAccess";
      }
      if (error.response?.headers?.server) {
        (error as Error & { serverHeader?: unknown }).serverHeader = error.response.headers.server;
      }
    } else if (error instanceof Error) {
      console.error("Error making request:", error.message);
    } else {
      console.error("Unknown error making request:", error);
    }
  }

  public async makeRequest(
    endpoint: string,
    method: Method,
    data?: string | FormData,
    options?: MakeRequestOptions,
    signal?: AbortSignal,
  ) {
    const headers = this.buildHeaders(data);
    const params = this.buildParams(options);
    const responseType: ResponseType = options?.rawResponse ? "arraybuffer" : "json";

    try {
      const response = await axios({
        baseURL: APIClient.BACKEND_ROOT_PATH,
        url: endpoint,
        responseType: responseType,
        method: method,
        headers: headers,
        data: data,
        params: params,
        signal: signal,
      });

      const contentDisposition = response.headers["content-disposition"];
      if (contentDisposition?.includes("attachment")) {
        return this.handleAttachmentResponse(response, contentDisposition, options);
      }

      return response.data;
    } catch (error) {
      this.handleError(error, signal);
      throw error;
    }
  }
}

export const apiClient = new APIClient();
