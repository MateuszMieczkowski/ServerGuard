FROM mcr.microsoft.com/dotnet/runtime:8.0 AS base
USER app
WORKDIR /app

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
ARG BUILD_CONFIGURATION=Release
WORKDIR /src
COPY ["Directory.Packages.props", "."]
COPY ["Directory.Build.props", "."]
COPY ["nuget.config", "."]
COPY ["src/ServerGuard.Worker/ServerGuard.Worker.csproj", "src/ServerGuard.Worker/"]
COPY ["src/ServerGuard.Contracts/ServerGuard.Contracts.csproj", "src/ServerGuard.Contracts/"]
RUN dotnet restore "./src/ServerGuard.Worker/./ServerGuard.Worker.csproj"
COPY . .
WORKDIR "/src/src/ServerGuard.Worker"
RUN dotnet build "./ServerGuard.Worker.csproj" -c $BUILD_CONFIGURATION -o /app/build

FROM build AS publish
ARG BUILD_CONFIGURATION=Release
RUN dotnet publish "./ServerGuard.Worker.csproj" -c $BUILD_CONFIGURATION -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "ServerGuard.Worker.dll"]
