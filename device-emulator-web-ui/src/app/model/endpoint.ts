export class EndPoint {

    public static get CONNECTED(): string { return "CONNECTED"; }
    public static get CONNECTING(): string { return "CONNECTING"; }
    public static get UNRESPONSIVE(): string { return "UNRESPONSIVE"; }
    public static get DISCONNECTING(): string { return "DISCONNECTING"; }
    public static get DISCONNECTED(): string { return "DISCONNECTED"; }

    constructor(public name: string,
                public ip: string,
                public port: number,
                public status: string) {
    }

    canConnect(): boolean {
        return this.status == EndPoint.DISCONNECTED;
    }

    canDisconnect(): boolean {
        return this.status == EndPoint.CONNECTED
            || this.status == EndPoint.UNRESPONSIVE;
    }
}