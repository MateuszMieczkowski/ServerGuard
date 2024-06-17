FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
USER app
WORKDIR /app
EXPOSE 8080
EXPOSE 8081

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
ARG BUILD_CONFIGURATION=Release
WORKDIR /src
COPY ["Directory.Packages.props", "."]
COPY ["Directory.Build.props", "."]
COPY ["nuget.config", "."]
COPY ["src/ServerGuard.Api/ServerGuard.Api.csproj", "src/ServerGuard.Api/"]
COPY ["src/ServerGuard.Infrastructure/ServerGuard.Infrastructure.csproj", "src/ServerGuard.Infrastructure/"]
COPY ["src/ServerGuard.Core/ServerGuard.Core.csproj", "src/ServerGuard.Core/"]
COPY ["src/ServerGuard.UseCases/ServerGuard.UseCases.csproj", "src/ServerGuard.UseCases/"]
RUN dotnet restore "./src/ServerGuard.Api/./ServerGuard.Api.csproj"
COPY . .
WORKDIR "/src/src/ServerGuard.Api"
RUN dotnet build "./ServerGuard.Api.csproj" -c $BUILD_CONFIGURATION -o /app/build

FROM build AS publish
ARG BUILD_CONFIGURATION=Release
RUN dotnet publish "./ServerGuard.Api.csproj" -c $BUILD_CONFIGURATION -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "ServerGuard.Api.dll"]
