export class ObjectMerger {
  public static mergeDeep<T>(target: T, ...sources: unknown[]): T {
    if (!sources.length) return target;
    const source = sources.shift();

    if (this.isObject(target) && this.isObject(source)) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const targetObject = target as any;
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const sourceObject = source as any;
      for (const key in sourceObject) {
        if (this.isObject(sourceObject[key])) {
          if (!targetObject[key]) {
            Object.assign(targetObject, { [key]: {} });
          }
          this.mergeDeep(targetObject[key], sourceObject[key]);
        } else {
          Object.assign(targetObject, { [key]: sourceObject[key] });
        }
      }
    }

    return this.mergeDeep(target, ...sources);
  }

  private static isObject(item: unknown) {
    return item && typeof item === "object" && !Array.isArray(item);
  }
}
